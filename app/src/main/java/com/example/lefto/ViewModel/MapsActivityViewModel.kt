package com.example.lefto.ViewModel

import androidx.lifecycle.ViewModel
import com.example.lefto.model.RestaurantItem

class MapsActivityViewModel: ViewModel() {
    private var restaurantList: MutableList<RestaurantItem>? = null

    fun getRestaurantList(): MutableList<RestaurantItem>? {
        if (restaurantList == null) {
            loadRestaurantList()
        }

        return restaurantList
    }

    private fun loadRestaurantList() {
        TODO("Not yet implemented")
    }

}