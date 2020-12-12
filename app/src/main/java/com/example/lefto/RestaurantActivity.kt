package com.example.lefto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lefto.adapter.LeftoverAdapter
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.view.MapsActivity
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_restaurant.*

class RestaurantActivity : AppCompatActivity() {

//    var leftovers = mutableListOf(
//        LeftOverItem("Pastas","yummy",
//            false,true,2,resto),
//        LeftOverItem("Eggplants","not yummy",
//            true,true,2,resto),
//    )
    companion object {
        var resto = RestaurantItem("Tasty",0.0,0.0)
        lateinit var adapter : LeftoverAdapter
    }
    var leftovers = ArrayList<LeftOverItem>()
    var REQUEST_ADD_LEFTOVER = 120
    private val DAO = FirebaseUtils(this, Firebase.firestore)
    // TODO popups
    // TODO Database communication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val arrayAdapter = ArrayAdapter<LeftOverItem>(this,
            android.R.layout.simple_list_item_1,leftovers)
        list_leftovers.layoutManager = LinearLayoutManager(this)
        adapter = LeftoverAdapter(leftovers)
        list_leftovers.adapter = adapter

        txt_restau_name.text = resto.name
        btn_add_leftover.setOnClickListener {
//            leftovers.add(0, LeftOverItem("jesepo",
//                "nonplus",false,false,69,resto)
//            )
//            adapter.notifyDataSetChanged()
            val intent = Intent(this, AddLeftoverActivity::class.java);
            startActivityForResult(intent,REQUEST_ADD_LEFTOVER)
        }
    }

    override fun onResume() {
        super.onResume()
        // fetch data from DB
        DAO.getLeftovers(resto, leftovers)
    }
}