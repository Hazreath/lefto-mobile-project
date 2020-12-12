package com.example.lefto.utils

import android.app.Activity
import android.util.Log
import com.example.lefto.view.RestaurantActivity
import com.example.lefto.model.ClientItem
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FirebaseUtils(val activity: Activity, private val db: FirebaseFirestore) {

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
                GeneralUtils.showToast(activity,"Registered successfully !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding client", e)
                GeneralUtils.showToast(activity,"Error occured during registration.")
            }
    }

    fun addRestaurant(restaurant: RestaurantItem) {
        db.collection(RESTAURANT_COLLECTION)
            .add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Restaurant written with ID: ${documentReference.id}")
                GeneralUtils.showToast(activity,"Registered successfully !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding restaurant", e)
                GeneralUtils.showToast(activity,"Error occured during registration.")
            }
    }

    fun addLeftover(leftover: LeftOverItem) {
        db.collection(LEFTOVER_COLLECTION)
            .add(leftover)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Leftover written with ID: ${documentReference.id}")
                GeneralUtils.showToast(activity,"Leftover added !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding leftover", e)
                GeneralUtils.showToast(activity,"Error occured when adding the leftover.")
            }
    }

    fun getAllRestaurants() {
        val restaurantList = mutableListOf<RestaurantItem>()
        db.collection(RESTAURANT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val restaurant = document.toObject<RestaurantItem>()
                    if (restaurant != null) {
                        restaurant.id = document.id
                    }
                    Log.d(TAG, "$restaurant")
                    restaurant?.let { restaurantList.add(it) }
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun restaurantExists(restaurant : RestaurantItem) {
        db.collection(RESTAURANT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val r = document.toObject<RestaurantItem>()
                    Log.d(TAG, "$r")
                    Log.d(TAG, "${document.id} => ${document.data}")
                    if (r != null) {
                        if (restaurant.name == r.name) {
                            // Deep copy of r into restaurant
                            GeneralUtils.restaurantDeepCopy(restaurant,r)
                            restaurant.id = document.id
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun getLeftovers(restaurant: RestaurantItem, leftoverList : ArrayList<LeftOverItem>) {
//        val leftoverList = mutableListOf<LeftOverItem>()
        Log.d(TAG, "getLeftovers")
        db.collection(LEFTOVER_COLLECTION)
            .whereEqualTo("restaurantItem.name", restaurant.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val leftover = document.toObject<LeftOverItem>()
                    if (leftover != null) {
                        leftover.id = document.id
                    }
                    leftover.let { leftoverList.add(it) }
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                RestaurantActivity.adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun updateLeftoverQuantity(leftover: LeftOverItem) {
        Log.d(TAG,"UPDATE QTY $leftover.id")

        db.collection(LEFTOVER_COLLECTION).document(leftover.id).update(
            "quantity",leftover.quantity
        ).addOnSuccessListener {
            Log.d(TAG,"UPDATE QTY OK")
        }.addOnFailureListener {
            GeneralUtils.showToast(activity,"Error on database communication.")
        }
    }

    fun deleteLeftoverEntry(leftover: LeftOverItem) {
        db.collection(LEFTOVER_COLLECTION).document(leftover.id).delete().
        addOnSuccessListener {
            GeneralUtils.showToast(activity,"${leftover.name} deleted successfully !")
        }.addOnFailureListener {
            GeneralUtils.showToast(activity,"Error on database communication.")
        }
    }

    fun getRestaurantLinkedWithEmail(mail : String, restaurantItem: RestaurantItem) {
        db.collection(RESTAURANT_COLLECTION)
            .whereEqualTo("email", mail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val r = document.toObject<RestaurantItem>()
                    if (r != null) {
                        GeneralUtils.restaurantDeepCopy(restaurantItem, r)
                        restaurantItem.id = document.id
                    }
                    Log.d(RestaurantActivity.TAG, "${document.id} => ${document.data}")
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun getClientLinkedWithEmail(mail : String, client: ClientItem) {
        db.collection(CLIENT_COLLECTION)
            .whereEqualTo("email", mail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val c = document.toObject<ClientItem>()
                    if (c != null) {
                        GeneralUtils.clientDeepCopy(client, c)
                        client.id = document.id
                    }
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                RestaurantActivity.adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
