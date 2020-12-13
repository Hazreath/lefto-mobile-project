package com.example.lefto.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lefto.R
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.example.lefto.utils.GeneralUtils.Companion.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_login.sw_type
import kotlinx.coroutines.*
import java.lang.RuntimeException

class LoginActivity : AppCompatActivity() {
    private var switch_state = false // default : client
    private val DAO = FirebaseUtils(this,Firebase.firestore)

    companion object {
        private val TAG = LoginActivity::class.java.name
        val BUNDLE_RESTAURANT = "BUNDLE_RESTAURANT"
        val BUNDLE_INDEX_RESTAURANT = "restaurant"
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        // Client/Restaurant switch
        sw_type.setOnCheckedChangeListener {
                buttonView, isChecked ->

            switch_state = isChecked
            if (isChecked) {
                buttonView.text = "Restaurant"
            } else {
                buttonView.text = "Client"
            }
        }

        // Login button
        btn_login.setOnClickListener {
            if (validateForm()) {
                CoroutineScope(Dispatchers.Main).launch {
                    signIn(et_email.text.toString(), et_password.text.toString())
                }
            }
        }

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        signOut() // TODO: Remove later - for testing purposes -> log out every time the app starts
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private suspend fun signIn(email: String, password: String) {
        Log.d(TAG, "Signing in with email: $email")
        try {
        } catch (e: RuntimeException) {

        }
        if (switch_state) {
            if (!DAO.restaurantExists(email)) {
                Log.w(TAG, "Sign in with email: Failure")
                GeneralUtils.showToast(this, "Account does not exist")
                return
            }
        }
        else {
            Log.d(TAG, "Checking client: ${!DAO.clientExists(email)}")
            if (!DAO.clientExists(email)) {
                Log.w(TAG, "Sign in with email: Failure")
                GeneralUtils.showToast(this, "Account does not exist")
                return
            }
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in with email: Success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "Sign in with email: Failure")
                    GeneralUtils.showToast(this, "Account does not exists")
                    updateUI(null)
                }
            }
    }

    private fun validateForm(): Boolean {
        var empty = false
        var validMailFormat = false
        var passwordTooShort = false
        var inputs = arrayOf(
            et_email,
            et_password
        )
        // are fields empty ?
        inputs.forEach {
            if (it.text.toString().isNullOrEmpty()) {
                empty = true
            }
        }

        // mail format check
        val mail = et_email.text.toString()
        if (!mail.isNullOrEmpty()) {
            validMailFormat = GeneralUtils.isMailFormat(mail)
        }

        // Password length check
        passwordTooShort = et_password.text.toString().length < RegisterActivity.MIN_PASSWORD_LENGTH

        if (empty) {
            showToast(this,"One or more required fields are empty !")
        } else if (!validMailFormat) {
            showToast(this,"Wrong email format !")
        } else if (passwordTooShort) {
            GeneralUtils.showToast(this,"Password must be at least 6 characters !")
        }

        return !empty && validMailFormat && !passwordTooShort
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (switch_state) {
                // TODO Get restaurant linked to specified email
                var restaurant = RestaurantItem()
                val intent = Intent(this, RestaurantActivity::class.java)

                GlobalScope.launch {
                    suspend {
                        auth.currentUser?.email?.let {
                            DAO.getRestaurantLinkedWithEmail(it,restaurant)
                        }

                        // TODO better ^^'
                        while(restaurant.name=="");

                        intent.putExtra("name", restaurant.name)
                        intent.putExtra("id", restaurant.id)
                        Log.d(TAG,"intent with : ${restaurant}")
                        startActivity(intent)

                    }.invoke()
                }


            } else {
                // TODO Get client linked to specified email

                val intent = Intent(this, ClientActivity::class.java)
                startActivity(intent)
            }

        } else {
            et_password.setText("")
        }
    }
}