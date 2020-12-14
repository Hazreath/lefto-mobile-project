package com.example.lefto.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lefto.R
import com.example.lefto.adapter.LeftoverAdapter
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_restaurant.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val currentRestaurant = RestaurantItem()


    companion object {
        val TAG = "RESTAURANT ACTIVITY"
        var resto = RestaurantItem(name="Tasty")
        lateinit var adapter : LeftoverAdapter
        lateinit var DAO : FirebaseUtils
    }

    var leftovers = ArrayList<LeftOverItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        //var bundle = intent.getBundleExtra(LoginActivity.BUNDLE_RESTAURANT)
        if (currentRestaurant.name.isNullOrEmpty()) {
            Log.d(TAG,"Read intent")
            currentRestaurant.name = intent.getStringExtra("name").toString()
            currentRestaurant.id = intent.getStringExtra("id").toString()
        }

        Log.d("Login","Restaurant view : $currentRestaurant")
        DAO = FirebaseUtils(this, Firebase.firestore)

        setContentView(R.layout.activity_restaurant)
        Log.d(TAG,"Content view SET !")

        list_leftovers.layoutManager = LinearLayoutManager(this)
        adapter = LeftoverAdapter(leftovers)
        list_leftovers.adapter = adapter

        txt_restau_name.text = currentRestaurant.name
        btn_add_leftover.setOnClickListener {
            val intent = Intent(this, AddLeftoverActivity::class.java);
            intent.putExtra("name", currentRestaurant.name)
            intent.putExtra("id", currentRestaurant.id)
            startActivity(intent)
        }

        btn_disconnect.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
//        super.onBackPressed()
        moveTaskToBack(false)
    }
    override fun onResume() {
        super.onResume()
        // fetch data from DB
        leftovers.clear()
        DAO.getLeftovers(currentRestaurant, leftovers, adapter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("id",currentRestaurant.id)
            putString("name",currentRestaurant.name)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG,"restored !")
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            currentRestaurant.id = getString("id").toString()
            currentRestaurant.name = getString("name").toString()
        }
    }
}