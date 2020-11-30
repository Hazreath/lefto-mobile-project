package com.example.lefto.model

data class LeftOverItem(
    var name: String = "",
    var description: String = "",
    var vegan : Boolean = false,
    var halal: Boolean = false,
    var quantity: Int = 0,
    var restaurantItem: RestaurantItem = RestaurantItem()
)
