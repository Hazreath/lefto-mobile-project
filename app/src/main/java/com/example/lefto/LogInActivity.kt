package com.example.lefto

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LogInActivity: AppCompatActivity() {

    companion object {
        private val TAG = LogInActivity::class.java.name
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setLoginButtonListener()

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        signOut() // TODO: Remove later - for testing purposes -> log out every time the app starts
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "Signing in with email: $email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in with email: Success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "Sign in with email: Failure")
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
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        } else {
            et_password.setText("")
        }
    }

    private fun setLoginButtonListener() {
        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            signIn(email, password)
        }
    }
}