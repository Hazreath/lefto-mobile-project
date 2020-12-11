package com.example.lefto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.lefto.model.ClientItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.example.lefto.view.MapsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private var switch_state = false // default : client
    private val DAO = FirebaseUtils(this,Firebase.firestore)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setRestaurantUI(View.INVISIBLE)

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
            if (switch_state && checkRestauRegisterInputs()) { // Restau

                var latlng = arrayOf(
                    et_latlong.text.split(';')[0].toDouble(),
                    et_latlong.text.split(';')[1].toDouble()
                )
                var r = RestaurantItem(
                    et_restau_name.text.toString(),
                    latlng[0],
                    latlng[1],
                    et_type.text.toString(),
                    chk_vegan.isChecked,
                    chk_halal.isChecked
                )

                DAO.addRestaurant(r)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else if (checkClientRegisterInputs()) { // Client
                var c = ClientItem(
                    et_email.text.toString(),
                    et_city.text.toString()
                )
                DAO.addClient(c)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun setRestaurantUI(visibility : Int) {
        et_latlong.visibility = visibility
        et_type.visibility = visibility
        et_restau_name.visibility = visibility
        layout_chk_restau.visibility = visibility
    }

    fun setClientUI(visibility: Int) {
        et_city.visibility = visibility
    }

    fun checkRestauRegisterInputs() :Boolean {
        var empty = false
        var validMailFormat = false
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
            validMailFormat = android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
        }

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
        } else if (!validCoords) {
            GeneralUtils.showToast(this,"Wrong coordinates format !")
        }
        return !empty && validMailFormat && validCoords
    }

    fun checkClientRegisterInputs() : Boolean {
        var empty = false
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
            validMailFormat = android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
        }

        if (empty) {
            GeneralUtils.showToast(this,"One or more required fields are empty !")
        } else if (!validMailFormat) {
            GeneralUtils.showToast(this,"Wrong email format !")
        }

        return !empty && validMailFormat
    }
}