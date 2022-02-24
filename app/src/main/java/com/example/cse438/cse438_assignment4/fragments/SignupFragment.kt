package com.example.cse438.cse438_assignment4.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cse438.cse438_assignment4.BlackjackActivity
import com.example.cse438.cse438_assignment4.MainActivity
import com.example.cse438.cse438_assignment4.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_signup.*


class SignupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    fun setFirebase(auth: FirebaseAuth){
        this.auth = auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val intent = Intent(this.context, BlackjackActivity::class.java)

        button_create.setOnClickListener{
            createAccount(signup_field_email.text.toString(), signup_field_password.text.toString(), intent)
        }

    }

    /**
     * if an account doesn't exist
     * create an account in Firebase
     */
    private fun createAccount(email: String, password: String, intent: Intent) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * from Google
     * makes sure the fields are not null
     */
    private fun validateForm(): Boolean {
        var valid = true

        val email = signup_field_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            signup_field_email.error = "Required."
            valid = false
        } else {
            signup_field_email.error = null
        }

        val password = signup_field_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            signup_field_password.error = "Required."
            valid = false
        } else {
            signup_field_password.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "Signup"
    }
}
