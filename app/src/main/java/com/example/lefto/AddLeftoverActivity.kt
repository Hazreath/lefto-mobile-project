package com.example.lefto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.utils.GeneralUtils
import com.example.lefto.view.MapsActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_leftover.*
import kotlinx.android.synthetic.main.activity_add_leftover.chk_halal
import kotlinx.android.synthetic.main.activity_add_leftover.chk_vegan
import kotlinx.android.synthetic.main.activity_register.*

class AddLeftoverActivity : AppCompatActivity() {
    private val currentUser = RestaurantActivity.resto
    private val DAO = FirebaseUtils(this, Firebase.firestore)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leftover)

        btn_submit_leftover.setOnClickListener {
            if (inputCheck()) {
                val l = LeftOverItem(
                    et_name.text.toString(),
                    et_description.text.toString(),
                    chk_vegan.isChecked,
                    chk_halal.isChecked,
                    Integer.parseInt(et_quantity.text.toString()),
                    currentUser
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