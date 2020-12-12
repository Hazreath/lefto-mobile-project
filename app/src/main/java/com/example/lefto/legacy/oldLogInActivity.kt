package com.example.lefto.legacy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lefto.R
import com.example.lefto.view.ClientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class oldLogInActivity: AppCompatActivity() {

    companion object {
        private val TAG = oldLogInActivity::class.java.name
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
            val intent = Intent(this, ClientActivity::class.java)
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