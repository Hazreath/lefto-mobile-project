package com.example.lefto

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.GeneralUtils.Companion.showToast
import com.example.lefto.view.MapsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var switch_state = false // default : client
    companion object {
        private val TAG = LoginActivity::class.java.name
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setLoginButtonListener()

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
            if (switch_state) { // Restaurant logic
                // TODO Login

                if (true) { // Logged
                    // TODO forward needed intents
                    val idRestau = 4

                    val intent = Intent(this, RestaurantActivity::class.java).apply {
                        putExtra(getString(R.string.intent_login_id_restaurant),
                            idRestau)
                    }
                    startActivity(intent)
                } else {
                    showToast(this,getString(R.string.err_invalid_credentials))
                }
            } else { // Client logic

                if (true) {
                    val idClient = 4

                    val intent = Intent(this, MapsActivity::class.java).apply {
                        putExtra(getString(R.string.intent_login_id_restaurant),
                            idClient)
                    }
                    startActivity(intent)
                }
            }
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

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "Signing in with email: $email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in with email: Success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "Sign in with email: Failure")
                    Toast.makeText(baseContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun validateForm(): Boolean {
        // TODO:
        return true
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        } else {
            et_password.setText("")
        }
    }

    private fun setLoginButtonListener() {
        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            signIn(email, password)
        }
    }
}