package com.example.lefto.model

data class RestaurantItem(
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var type: String = "",
    var vegan: Boolean = false,
    var halal: Boolean = false
)

