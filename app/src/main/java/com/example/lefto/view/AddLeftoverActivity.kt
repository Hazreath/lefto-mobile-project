package com.example.lefto.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.example.lefto.R
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_leftover.*
import kotlinx.android.synthetic.main.activity_add_leftover.chk_halal
import kotlinx.android.synthetic.main.activity_add_leftover.chk_vegan

class AddLeftoverActivity : AppCompatActivity() {
    private val currentRestaurant = RestaurantItem()
    private val DAO = FirebaseUtils(this, Firebase.firestore)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leftover)

        currentRestaurant.name = intent.getStringExtra("name").toString()
        currentRestaurant.id = intent.getStringExtra("id").toString()

        btn_submit_leftover.setOnClickListener {
            if (inputCheck()) {
                val l = LeftOverItem(
                    name=et_name.text.toString(),
                    description = et_description.text.toString(),
                    vegan = chk_vegan.isChecked,
                    halal = chk_halal.isChecked,
                    quantity = Integer.parseInt(et_quantity.text.toString()),
                    restaurantItem = currentRestaurant
                )

                // Add to db
                DAO.addLeftover(l)

                // BUGFIX : RestaurantActivity is reading the extras of this intent on
                //
                val intent = Intent(this, RestaurantActivity::class.java)
                intent.putExtra("name", currentRestaurant.name)
                intent.putExtra("id", currentRestaurant.id)
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