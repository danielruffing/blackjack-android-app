package com.example.cse438.cse438_assignment4

import android.os.Bundle
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment4.adapter.LeaderboardListAdapter
import com.example.cse438.cse438_assignment4.util.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_leaderboard.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor

/**
 * displays all users from Firestore
 * into a recycler view
 */
class LeaderboardActivity : AppCompatActivity(){
    var leaderboardList: ArrayList<User> = ArrayList()
    lateinit var db : FirebaseFirestore

    companion object {
        val TAG = "Firestore"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()
    }

    override fun onStart() {
        super.onStart()

        //set recycler view
        val adapter = LeaderboardListAdapter(leaderboardList)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)

        db.collection("users")
            .get()
            .addOnSuccessListener {result ->
                for(document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    leaderboardList.add(
                        User(
                            document.id,
                            document["chips"].toString().toInt(),
                            document["wins"].toString().toInt(),
                            document["losses"].toString().toInt()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                println("failed to retrieve data")
            }
    }
}

