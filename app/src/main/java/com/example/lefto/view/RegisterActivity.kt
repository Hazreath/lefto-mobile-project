package com.example.lefto.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.lefto.R
import com.example.lefto.model.ClientItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private var switch_state = false // default : client
    private val DAO = FirebaseUtils(this,Firebase.firestore)
    private lateinit var auth: FirebaseAuth
    private val TAG = "REGISTER : "

    companion object {
       const val MIN_PASSWORD_LENGTH = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setRestaurantUI(View.INVISIBLE)

        auth = Firebase.auth

        sw_type.setOnCheckedChangeListener {
                buttonView, isChecked ->
            switch_state = isChecked
            if (isChecked) { // Restaurant UI display
                // Hide client UI
                buttonView.text = "Restaurant"
                setClientUI(View.INVISIBLE)
                setRestaurantUI(View.VISIBLE)
            } else { // Client UI display
                // Hide restau UI
                buttonView.text = "Client"
                setRestaurantUI(View.INVISIBLE)
                setClientUI(View.VISIBLE)
            }
        }

        btn_register.setOnClickListener {
            // TODO check client and restaurant unicity (email field)
            if (switch_state && checkRestauRegisterInputs()) { // Restau
                var email = et_email.text.toString()
                var password = et_password.text.toString()

                var latlng = arrayOf(
                    et_latlong.text.split(';')[0].toDouble(),
                    et_latlong.text.split(';')[1].toDouble()
                )

                var r = RestaurantItem(
                    email = email,
                    name = et_restau_name.text.toString(),
                    latitude = latlng[0],
                    longitude = latlng[1],
                    type = et_type.text.toString(),
                    vegan = chk_vegan.isChecked,
                    halal = chk_halal.isChecked
                )

                DAO.addRestaurant(r)
                GlobalScope.launch {
                    suspend {
                        addFirebaseUser(email,password)

                        withContext(Dispatchers.Main) {

                            returnToLoginScreen()
                        }
                    }.invoke()
                }

            } else if (checkClientRegisterInputs()) { // Client
                var email = et_email.text.toString()
                var password = et_password.text.toString()
                var c = ClientItem(
                    email = email,
                    city = et_city.text.toString()
                )
                DAO.addClient(c)
                GlobalScope.launch {
                    suspend {
                        addFirebaseUser(email,password)

                        withContext(Dispatchers.Main) {

                            returnToLoginScreen()
                        }
                    }.invoke()
                }
            }
        }
    }
    
    private fun returnToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun setRestaurantUI(visibility : Int) {
        et_latlong.visibility = visibility
        et_type.visibility = visibility
        et_restau_name.visibility = visibility
        layout_chk_restau.visibility = visibility
    }

    fun setClientUI(visibility: Int) {
        et_city.visibility = visibility
    }

    private fun checkRestauRegisterInputs() : Boolean {
        var empty = false
        var validMailFormat = false
        var passwordTooShort = false
        var validCoords = false
        var inputs = arrayOf(
            et_email,
            et_password,
            et_latlong,
            et_restau_name,
            et_type
        )

        // are fields empty ?
        inputs.forEach {
            if (it.text.toString().isNullOrEmpty()) {
                empty = true
            }
        }

        // mail format check
        val mail = inputs[0].text.toString()
        if (!mail.isNullOrEmpty()) {
            validMailFormat = GeneralUtils.isMailFormat(mail)
        }

        // password length check
        passwordTooShort = et_password.text.toString().length < MIN_PASSWORD_LENGTH

        // Coordinates check
        val regex = "^-?\\d*.\\d*;-?\\d*.\\d*".toRegex()
        val coords = et_latlong.text.toString()
        if (!coords.isNullOrEmpty()) {
            validCoords = regex.matches(coords)
        }

        if (empty) {
            GeneralUtils.showToast(this,"One or more required fields are empty !")
        } else if (!validMailFormat) {
            GeneralUtils.showToast(this,"Wrong email format !")
        }  else if (passwordTooShort) {
            GeneralUtils.showToast(this,"Password must be at least 6 characters !")
        } else if (!validCoords) {
            GeneralUtils.showToast(this,"Wrong coordinates format !")
        }
        return !empty && validMailFormat && validCoords && !passwordTooShort
    }

    private fun checkClientRegisterInputs() : Boolean {
        var empty = false
        var passwordTooShort = false
        var validMailFormat = false
        var inputs = arrayOf(
            et_email,
            et_password,
            et_city
        )
        // are fields empty ?
        inputs.forEach {
            if (it.text.toString().isNullOrEmpty()) {
                empty = true
            }
        }

        // mail format check
        val mail = inputs[0].text.toString()
        if (!mail.isNullOrEmpty()) {
            validMailFormat = GeneralUtils.isMailFormat(mail)
        }

        // password length check
        passwordTooShort = et_password.text.toString().length < MIN_PASSWORD_LENGTH

        if (empty) {
            GeneralUtils.showToast(this,"One or more required fields are empty !")
        } else if (!validMailFormat) {
            GeneralUtils.showToast(this,"Wrong email format !")
        } else if (passwordTooShort) {
            GeneralUtils.showToast(this,"Password must be at least 6 characters !")
        }

        return !empty && validMailFormat && !passwordTooShort
    }


    fun addFirebaseUser(email : String, password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Create user with email: Success")
                    val user = auth.currentUser

                } else {
                    Log.d(TAG, "Create user with email: Failure ")
                    GeneralUtils.showToast(this, "Create user with email: Failure ")
                    Toast.makeText(baseContext, "Authentication failed!", Toast.LENGTH_SHORT).show()

                }
            }
    }
}