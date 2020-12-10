package com.example.lefto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lefto.adapter.LeftoverAdapter
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.google.android.gms.common.internal.FallbackServiceBroker
import kotlinx.android.synthetic.main.activity_restaurant.*

class RestaurantActivity : AppCompatActivity() {
    var resto = RestaurantItem("Siussmabyt",0.0,0.0)
    var leftovers = mutableListOf(
        LeftOverItem("Pastas","yummy",
            false,true,2,resto),
        LeftOverItem("Eggplants","not yummy",
            true,true,2,resto),
    )
    // TODO popups
    // TODO Database communication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val arrayAdapter = ArrayAdapter<LeftOverItem>(this,
            android.R.layout.simple_list_item_1,leftovers)
        list_leftovers.layoutManager = LinearLayoutManager(this)
        val adapter = LeftoverAdapter(leftovers)
        list_leftovers.adapter = adapter

        txt_restau_name.text = resto.name
        btn_add_leftover.setOnClickListener {
            leftovers.add(0, LeftOverItem("jesepo",
                "nonplus",false,false,69,resto)
            )
            adapter.notifyDataSetChanged()
        }
    }
}