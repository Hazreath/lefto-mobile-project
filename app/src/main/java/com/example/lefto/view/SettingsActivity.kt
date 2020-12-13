package com.example.lefto.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lefto.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    lateinit var editor: SharedPreferences.Editor
    lateinit var PREFERENCES : SharedPreferences

    companion object {
        val SETTINGS_REQUEST_CODE = 155
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        PREFERENCES = getSharedPreferences(
            getString(R.string.pref_filename),
            Context.MODE_PRIVATE
        )

        // Set values to options
        sw_location.isChecked = PREFERENCES.getBoolean("WIFILocation",false)
        sw_halal.isChecked = PREFERENCES.getBoolean("onlyHalal",false)
        sw_vegan.isChecked = PREFERENCES.getBoolean("onlyVegan",false)


        editor = PREFERENCES.edit()

        btn_save_settings.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        saveSettings()
        super.onBackPressed()

    }

    fun saveSettings() {
        editor.putBoolean("WIFILocation",sw_location.isChecked)
        editor.putBoolean("onlyHalal",sw_halal.isChecked)
        editor.putBoolean("onlyVegan",sw_vegan.isChecked)
        editor.commit()
    }
}