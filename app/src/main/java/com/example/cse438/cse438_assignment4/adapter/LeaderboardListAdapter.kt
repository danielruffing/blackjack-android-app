package com.example.cse438.cse438_assignment4.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse438.cse438_assignment4.R
import com.example.cse438.cse438_assignment4.util.User

//define the binding for the view holder
class LeaderboardViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.leaderboard_list_item, parent, false)) {
    private val accountView: TextView
    private val chipsView: TextView
    private val winsView: TextView
    private val lossesView: TextView

    init {
        accountView = itemView.findViewById(R.id.leaderboard_account)
        chipsView = itemView.findViewById(R.id.leaderboard_chips)
        winsView = itemView.findViewById(R.id.leaderboard_wins)
        lossesView = itemView.findViewById(R.id.leaderboard_losses)
    }

    fun bind(user: User) {
        accountView.text = user.email
        chipsView.text = user.chips.toString()
        winsView.text = user.wins.toString()
        lossesView.text = user.losses.toString()
    }
}


//define the adapter for the recycler view
class LeaderboardListAdapter(private val list: ArrayList<User>) : RecyclerView.Adapter<LeaderboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LeaderboardViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val user: User = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = list.size

}