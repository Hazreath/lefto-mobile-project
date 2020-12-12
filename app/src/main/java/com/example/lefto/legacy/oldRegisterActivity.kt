package com.example.lefto.legacy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lefto.view.ClientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class oldRegisterActivity: AppCompatActivity() {
    companion object {
        private val TAG = oldRegisterActivity::class.java.name
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: setContentView(R.layout.activity_register)
        auth = Firebase.auth

    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "Created account: $email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Create user with email: Success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "Create user with email: Failure ")
                    Toast.makeText(baseContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun validateForm(): Boolean {
        // TODO:
        return true
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, ClientActivity::class.java)
            startActivity(intent)
        } else {
            et_password.setText("")
        }
    }
}