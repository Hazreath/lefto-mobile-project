package com.example.lefto.utils

import android.util.Log
import com.example.lefto.model.ClientItem
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.view.LogInActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FirebaseUtils(private val db: FirebaseFirestore) {

    companion object {
        private val TAG = FirebaseUtils::class.java.name
        const val CLIENT_COLLECTION = "clients"
        const val RESTAURANT_COLLECTION = "restaurants"
        const val LEFTOVER_COLLECTION = "leftovers"
    }

    fun addClient(client: ClientItem) {
        db.collection(CLIENT_COLLECTION)
            .add(client)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Client written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding client", e)
            }
    }

    fun addRestaurant(restaurant: RestaurantItem) {
        db.collection(RESTAURANT_COLLECTION)
            .add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Restaurant written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding restaurant", e)
            }
    }

    fun addLeftover(leftover: LeftOverItem) {
        db.collection(LEFTOVER_COLLECTION)
            .add(leftover)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Leftover written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding leftover", e)
            }
    }

    fun getAllRestaurants() {
        val restaurantList = mutableListOf<RestaurantItem>()
        db.collection(RESTAURANT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val restaurant = document.toObject<RestaurantItem>()
                    Log.d(TAG, "$restaurant")
                    restaurant?.let { restaurantList.add(it) }
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun getLeftovers(restaurant: RestaurantItem) {
        val leftoverList = mutableListOf<LeftOverItem>()
        db.collection(LEFTOVER_COLLECTION)
            .whereEqualTo("restaurantItem.name", restaurant.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val leftover = document.toObject<LeftOverItem>()
                    leftover.let { leftoverList.add(it) }
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
