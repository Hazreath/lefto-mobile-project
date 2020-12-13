package com.example.lefto.utils

import android.app.Activity
import android.widget.Toast
import com.example.lefto.model.ClientItem
import com.example.lefto.model.RestaurantItem


class GeneralUtils {

    companion object {
        fun showToast(act: Activity, text: String) {
            Toast.makeText(
                act, text, Toast.LENGTH_SHORT
            ).show()
        }

        fun isMailFormat(mail : String) : Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
        }
        fun restaurantDeepCopy(toOverwrite : RestaurantItem, toCopy : RestaurantItem) {
            toOverwrite.name = toCopy.name
            toOverwrite.email = toCopy.email
            toOverwrite.latitude = toCopy.latitude
            toOverwrite.longitude = toCopy.longitude
            toOverwrite.type = toCopy.type
            toOverwrite.vegan = toCopy.vegan
            toOverwrite.halal = toCopy.halal
        }

        fun clientDeepCopy(toOverwrite : ClientItem, toCopy : ClientItem) {
            toOverwrite.email = toCopy.email
            toOverwrite.city = toCopy.email
        }
    }

}