package com.example.lefto.utils

import android.app.Activity
import android.util.Log
import com.example.lefto.view.RestaurantActivity
import com.example.lefto.model.ClientItem
import com.example.lefto.model.LeftOverItem
import com.example.lefto.model.RestaurantItem
import com.example.lefto.view.ClientActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirebaseUtils(val activity: Activity, private val db: FirebaseFirestore) {

    companion object {
        private val TAG = FirebaseUtils::class.java.name
        const val CLIENT_COLLECTION = "clients"
        const val RESTAURANT_COLLECTION = "restaurants"
        const val LEFTOVER_COLLECTION = "leftovers"
    }

    fun addClient(client: ClientItem) {
        val newClientRef = db.collection(CLIENT_COLLECTION).document()
        client.id = newClientRef.id
        newClientRef
            .set(client)
            .addOnSuccessListener {
                Log.d(TAG, "Client written with ID: ${client.id}")
                GeneralUtils.showToast(activity,"Registered successfully !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding client", e)
                GeneralUtils.showToast(activity,"Error occured during registration.")
            }
    }

    fun addRestaurant(restaurant: RestaurantItem) {
        val newRestaurantRef = db.collection(RESTAURANT_COLLECTION).document()
        restaurant.id = newRestaurantRef.id
        newRestaurantRef
            .set(restaurant)
            .addOnSuccessListener {
                Log.d(TAG, "Restaurant written with ID: ${restaurant.id}")
                GeneralUtils.showToast(activity,"Registered successfully !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding restaurant", e)
                GeneralUtils.showToast(activity,"Error occured during registration.")
            }
    }

    fun addLeftover(leftover: LeftOverItem) {
        val newLeftoverRef = db.collection(LEFTOVER_COLLECTION).document()
        leftover.id = newLeftoverRef.id
        newLeftoverRef
            .set(leftover)
            .addOnSuccessListener {
                Log.d(TAG, "Leftover written with ID: ${leftover.id}")
                GeneralUtils.showToast(activity,"Leftover added !")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding leftover", e)
                GeneralUtils.showToast(activity,"Error occured when adding the leftover.")
            }
    }

    fun getAllRestaurants(restaurantList : ArrayList<RestaurantItem>, fetched : Boolean) {

        db.collection(RESTAURANT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                Log.d("BENJI", "onSuccess")
                for (document in result.documents) {
                    val restaurant = document.toObject<RestaurantItem>()
                    if (restaurant != null) {
                        Log.d("BENJI", "$restaurant")
                        restaurant.id = document.id
                        restaurant?.let { restaurantList.add(it) }
                    }
                    Log.d(TAG, "$restaurant")

                    Log.d(TAG, "${document.id} => ${document.data}")
                }

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
            .addOnCompleteListener() {
                ClientActivity.restauFetched = true
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

    suspend fun clientExists(client: ClientItem): Boolean {
        return db.collection(CLIENT_COLLECTION).document(client.id).get().await().exists()
    }

    suspend fun clientExists(email: String): Boolean {
        return !db.collection(CLIENT_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .await().isEmpty
    }

    suspend fun restaurantExists(restaurant: RestaurantItem): Boolean {
        return db.collection(RESTAURANT_COLLECTION).document(restaurant.id).get().await().exists()
    }

    suspend fun restaurantExists(email: String): Boolean {
        return !db.collection(RESTAURANT_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .await().isEmpty
    }
}
