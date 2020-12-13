package com.example.lefto.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.utils.FirebaseUtils
import com.example.lefto.view.ClientActivity
import com.example.lefto.view.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ClientActivityViewModel: ViewModel() {
    private var restaurantList = ArrayList<RestaurantItem>()
    private var leftOverList: MutableList<LeftOverItem>? = null
    private var DAO = ClientActivity.DAO

    suspend fun getRestaurantList(): ArrayList<RestaurantItem>? {
        Log.d("BENJI","get restau list")
        if (restaurantList.size == 0) {
            Log.d("BENJI","need to load")
            loadRestaurantList()


        }

        return restaurantList
    }

    fun getLeftOverList(): MutableList<LeftOverItem>? {
        if (leftOverList == null) {
            loadLeftOverList()
        }

        return leftOverList
    }

    private fun loadRestaurantList() {

        var fetched = false
        GlobalScope.launch {
            restaurantList?.let {
                DAO.getAllRestaurants(restaurantList,fetched)
            }
        }

        Log.d("BENJI","restaurants : $restaurantList")


    }

    private fun loadLeftOverList() {
        val restaurant = RestaurantItem(name="La Dolce Vita", latitude = 58.38217369982617, longitude = 26.722880700728307)

        val restaurant2 = RestaurantItem(name="Mandala", latitude = 58.381034174116714, longitude=26.722682379870015)

        leftOverList = mutableListOf(
            LeftOverItem(name="something", description = "with a description",
                vegan = true,
                halal = false,
                quantity = 5,
                restaurantItem = restaurant
            ),
            LeftOverItem(name="something", description = "with a description",
                vegan = true,
                halal = false,
                quantity = 5,
                restaurantItem = restaurant2
            )
        )
    }

}