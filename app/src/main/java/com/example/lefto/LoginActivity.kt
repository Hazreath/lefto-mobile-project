package com.example.lefto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sw_type.setOnCheckedChangeListener {
                buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "Restaurant"
            } else {
                buttonView.text = "Client"
            }
        }

        // TODO DB requests & intent forward to correct GUI
    }
}