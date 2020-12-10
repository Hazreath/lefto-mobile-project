package com.example.lefto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.lefto.model.RestaurantItem
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private var switch_state = false // default : client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setRestaurantUI(View.INVISIBLE)
        Log.d("ALED","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")

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
            if (switch_state) { // Restau
                checkRestauRegisterInputs()
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
                // TODO addRestaurant(r)
            } else { // Client

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

    fun checkRestauRegisterInputs() {
        // TODO
    }
}