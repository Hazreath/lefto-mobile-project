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
        val restaurant = RestaurantItem("La Dolce Vita", 58.38217369982617, 26.722880700728307, "", false, false)

        val restaurant2 = RestaurantItem("Mandala", 58.381034174116714, 26.722682379870015, "", false, false)

        leftOverList = mutableListOf(
            LeftOverItem("something", "with a description", true,
                halal = false,
                quantity = 5,
                restaurantItem = restaurant
            ),
            LeftOverItem("something", "with a description", true,
                halal = false,
                quantity = 5,
                restaurantItem = restaurant2
            )
        )
    }

}