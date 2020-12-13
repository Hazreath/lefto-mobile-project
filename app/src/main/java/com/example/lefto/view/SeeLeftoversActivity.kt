package com.example.lefto.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lefto.R
import com.example.lefto.adapter.LeftoverAdapter
import com.example.lefto.adapter.SeeLeftoverAdapter
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.android.synthetic.main.activity_see_leftovers.*
import kotlinx.android.synthetic.main.list_see_leftover_layout.*

class SeeLeftoversActivity : AppCompatActivity() {

    var id_restaurant = ""
    var name_restaurant = ""
    lateinit var currentRestaurant : RestaurantItem
    val leftovers = ArrayList<LeftOverItem>()

    companion object {
        lateinit var adapter : SeeLeftoverAdapter
        lateinit var DAO : FirebaseUtils
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DAO = FirebaseUtils(this, Firebase.firestore)

        super.onCreate(savedInstanceState)

        if (id_restaurant.isNullOrEmpty() && name_restaurant.isNullOrEmpty()) {
            Log.d("BENJI","intent get")
            id_restaurant = intent.getStringExtra("restaurant_id").toString()
            name_restaurant = intent.getStringExtra("restaurant_name").toString()

            Log.d("BENJI","$id_restaurant $name_restaurant")
        }



        currentRestaurant = RestaurantItem(id=id_restaurant, name=name_restaurant)


        // setup adapter
        setContentView(R.layout.activity_see_leftovers)

        tv_restau_name.text = name_restaurant
        list_see_leftovers.layoutManager = LinearLayoutManager(this)
        adapter = SeeLeftoverAdapter(leftovers)
        list_see_leftovers.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        // fetch data from DB
        leftovers.clear()
        DAO.getLeftovers(currentRestaurant, leftovers, adapter)
    }
}