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
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    fun setFirebase(auth: FirebaseAuth){
        this.auth = auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onStart() {
        super.onStart()
        //intent to the create exercise activity
        val intent = Intent(this.context, BlackjackActivity::class.java)

        val currentUser = auth.getCurrentUser()
        if(currentUser!=null){
            startActivity(intent)
        }

        button_login.setOnClickListener{
            signIn(login_field_email.text.toString(), login_field_password.text.toString(), intent)
        }
    }

    /**
     * signs the user into Firebase
     * starts Blackjack Activity
     */
    private fun signIn(email: String, password: String, intent: Intent) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
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

        val email = login_field_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            login_field_email.error = "Required."
            valid = false
        } else {
            login_field_email.error = null
        }

        val password = login_field_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            login_field_password.error = "Required."
            valid = false
        } else {
            login_field_password.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "Login"
    }
}
