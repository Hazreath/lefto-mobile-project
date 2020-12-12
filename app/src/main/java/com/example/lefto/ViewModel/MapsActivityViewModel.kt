package com.example.lefto.ViewModel

import androidx.lifecycle.ViewModel
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem

class MapsActivityViewModel: ViewModel() {
    private var restaurantList: MutableList<RestaurantItem>? = null
    private var leftOverList: MutableList<LeftOverItem>? = null

    fun getRestaurantList(): MutableList<RestaurantItem>? {
        if (restaurantList == null) {
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
        TODO("Not yet implemented")
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