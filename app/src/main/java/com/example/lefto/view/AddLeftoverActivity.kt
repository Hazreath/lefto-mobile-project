package com.example.lefto.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lefto.R
import com.example.lefto.model.LeftOverItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_leftover.*
import kotlinx.android.synthetic.main.activity_add_leftover.chk_halal
import kotlinx.android.synthetic.main.activity_add_leftover.chk_vegan

class AddLeftoverActivity : AppCompatActivity() {
    private val currentUser = RestaurantActivity.resto
    private val DAO = FirebaseUtils(this, Firebase.firestore)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leftover)

        btn_submit_leftover.setOnClickListener {
            if (inputCheck()) {
                val l = LeftOverItem(
                    name=et_name.text.toString(),
                    description = et_description.text.toString(),
                    vegan = chk_vegan.isChecked,
                    halal = chk_halal.isChecked,
                    quantity = Integer.parseInt(et_quantity.text.toString()),
                    restaurantItem = currentUser
                )

                // Add to db
                DAO.addLeftover(l)

                val intent = Intent(this, RestaurantActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun inputCheck() : Boolean {
        var empty = false
        var validMailFormat = false
        var validCoords = false
        var inputs = arrayOf(
            et_name,
            et_description,
            et_quantity
        )
        // are fields empty ?
        inputs.forEach {
            if (it.text.toString().isNullOrEmpty()) {
                empty = true
            }
        }

        if (empty) {
            GeneralUtils.showToast(this,"One or more required fields are empty !")
        }
        return !empty
    }
}