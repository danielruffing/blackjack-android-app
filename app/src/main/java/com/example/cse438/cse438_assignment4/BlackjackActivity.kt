package com.example.cse438.cse438_assignment4

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.cse438.cse438_assignment4.util.CardRandomizer
import com.example.cse438.cse438_assignment4.util.Hand
import com.example.cse438.cse438_assignment4.util.House
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_blackjack.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * runs the Blackjack game and saves user game info upon
 * signout or going to the leaderboard
 */
class BlackjackActivity : AppCompatActivity() {
    companion object {
        private val TAG = "Firestore"
    }

    //create a player and store in database
    var player = Hand()

    //create the dealer
    var house = House()

    private lateinit var auth: FirebaseAuth
    private lateinit var mDetector: GestureDetectorCompat
    private var height: Int = 0
    private var width: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        //Gestures
        mDetector = GestureDetectorCompat(this, MyGestureListener())
        val metrics = this.resources.displayMetrics
        this.height = metrics.heightPixels
        this.width = metrics.widthPixels

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("users")

        //Firebase authentication
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val email = user?.email
            blackjack_name.text = email

            var playerMap: MutableMap<String, Any?> = HashMap()
            playerMap["chips"] = blackjack_chips.text.toString().toInt()
            playerMap["wins"] = blackjack_wins.text.toString().toInt()
            playerMap["losses"] = blackjack_losses.text.toString().toInt()

            val userRef = userCollection.document(email.toString())
            userRef.get()
                .addOnSuccessListener { document->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        blackjack_chips.text = document["chips"].toString()
                        blackjack_wins.text = document["wins"].toString()
                        blackjack_losses.text = document["losses"].toString()

                        player.chips = document["chips"].toString().toInt()
                        player.wins = document["wins"].toString().toInt()
                        player.losses = document["losses"].toString().toInt()
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    userCollection.document(email.toString())
                        .set(playerMap)
                        .addOnSuccessListener { Log.d(TAG, "Document successfully created!")  }
                        .addOnFailureListener { e -> Log.w(TAG, "Error creating document", e) }
                }

            //buttons
            button_logout.setOnClickListener {
                playerMap["chips"] = blackjack_chips.text.toString().toInt()
                playerMap["wins"] = blackjack_wins.text.toString().toInt()
                playerMap["losses"] = blackjack_losses.text.toString().toInt()

                userRef.update(playerMap)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                signOut()
            }
            button_leaderboard.setOnClickListener {
                playerMap["chips"] = blackjack_chips.text.toString().toInt()
                playerMap["wins"] = blackjack_wins.text.toString().toInt()
                playerMap["losses"] = blackjack_losses.text.toString().toInt()

                userRef.update(playerMap)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                //send to leaderboard activity
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
            }

        }

    }

    /**
     * signs the user out and
     * returns to MainActivity
     */
    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * moves an image view
     *(never used)
     */
    fun moveTo(cardView: ImageView, targetX: Float, targetY: Float) {
    //fun moveTo(targetX: Float, targetY: Float) {

        val animSetXY = AnimatorSet()

        val x = ObjectAnimator.ofFloat(
            cardView,
            "translationX",
            cardView.translationX,
            targetX
        )

        val y = ObjectAnimator.ofFloat(
            cardView,
            "translationY",
            cardView.translationY,
            targetY
        )

        animSetXY.playTogether(x, y)
        animSetXY.duration = 300
        animSetXY.start()


    }

    private inner class MyGestureListener: GestureDetector.SimpleOnGestureListener() {
        val randomizer = CardRandomizer()
        var cardList: ArrayList<Int> = randomizer.getIDs(this@BlackjackActivity)
        val rand = Random()

        private var bet: Int = 0
        private var swipedistance = 150

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            println("double tap")
            //moveTo(card_deck,-this@BlackjackActivity.width / 2f + card_deck.width / 2, this@BlackjackActivity.height / 2f - card_deck.height / 2f)
            if (player.chips > -1) {
                //deal
                if (player.newGame) {
                    blackjack_wins.text = player.wins.toString()
                    blackjack_losses.text = player.losses.toString()

                    if(blackjack_bet.text.toString()==""){
                        Toast.makeText(this@BlackjackActivity, "Please enter valid bet amount", Toast.LENGTH_LONG).show()
                    } else if(blackjack_bet.text.toString().toInt()>player.chips){
                        Toast.makeText(this@BlackjackActivity, "Please enter valid bet amount", Toast.LENGTH_LONG).show()
                    } else{
                        bet = blackjack_bet.text.toString().toInt()
                        var temp = player.chips
                        player.chips = temp - bet
                        blackjack_chips.text = player.chips.toString()

                        //init hands
                        player.newHand(rand, cardList)
                        house.newHand(rand, cardList)

                        //set card images
                        val id = R.drawable.back
                        houseA.setImageResource(id)
                        houseB.setImageResource(house.idList[1])
                        houseC.setImageDrawable(null)
                        houseD.setImageDrawable(null)
                        houseE.setImageDrawable(null)
                        houseF.setImageDrawable(null)
                        houseG.setImageDrawable(null)

                        playerA.setImageResource(player.idList[0])
                        playerB.setImageResource(player.idList[1])
                        playerC.setImageDrawable(null)
                        playerD.setImageDrawable(null)
                        playerE.setImageDrawable(null)
                        playerF.setImageDrawable(null)
                        playerG.setImageDrawable(null)

                        //update chips
                        //blackjack_chips.text = (player.chips-bet).toString()

                        //evalaute hand values
                        scoreAfterDeal(player, house, bet)
                    }
                } else {
                    if (!player.checkBust() && !player.checkBlackjack()) {
                        player.hit(rand, cardList)

                        if(player.index==3){
                            playerC.setImageResource(player.idList[player.index-1])
                        } else if(player.index==4){
                            playerD.setImageResource(player.idList[player.index-1])
                        } else if(player.index==5){
                            playerE.setImageResource(player.idList[player.index-1])
                        } else if(player.index==6){
                            playerF.setImageResource(player.idList[player.index-1])
                        } else if(player.index==7){
                            playerG.setImageResource(player.idList[player.index-1])
                        }
                        player.setStringListAfterHit(resources.getResourceEntryName(player.idList[player.index - 1]))
                        scoreAfterHit(player, house, bet)
                        println("player score: " + player.score)
                    }
                }

            }
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            println("stand")
            if (e2.x - e1.x > swipedistance) {
                if (!player.newGame) {
                    player.stand = true
                    player.newGame = true
                    houseTurn(player, house, bet)
                }
                //moveTo(this@BlackjackActivity.width / 2f - cardback.width / 2, 0f)
                return true
            }
            return false
        }
    }

    /**
     * handles dealing out the first two cards
     * and evaluating the score
     */
    fun scoreAfterDeal(player: Hand, house: House, bet: Int) {
        //player deal
        for (i in 0..(player.index - 1)) {
            player.setStringListAfterDeal(resources.getResourceEntryName(player.idList[i]), i)
        }
        //house deal
        for (i in 0..(house.index - 1)) {
            house.setStringListAfterDeal(resources.getResourceEntryName(house.idList[i]), i)
        }
        //check player hand value
        //if (player.blackjack && !player.newGame) {
        if (player.blackjack) {
            player.newGame=true
            player.chips = (player.chips + bet) + (bet * 2)
            Toast.makeText(this, "Blackjack! You Win!", Toast.LENGTH_SHORT).show()
            player.wins++
        } //check house hand value
        else if (house.blackjack && !player.newGame) {
            houseA.setImageResource(house.idList[0])
            Toast.makeText(this, "Blackjack, House Wins", Toast.LENGTH_SHORT).show()
            player.newGame=true
            player.losses++
        }
    }

    /**
     * handles dealing out a card after a hit
     * and evaluting the score
     */
    fun scoreAfterHit(player: Hand, house: House, bet: Int) {
        player.newGame=false
        //player.setStringListAfterHit(resources.getResourceEntryName(player.idList[player.index - 1]))
        if((player.stand && !player.bust)&& !player.newGame) {
            /**
             * player decided to stand
             * only the house hits
             */
            //house.setStringListAfterHit(resources.getResourceEntryName(house.idList[house.index - 1]))
            if (house.blackjack) {
                houseA.setImageResource(house.idList[0])
                player.newGame=true
                Toast.makeText(this, "Blackjack! House Win!", Toast.LENGTH_SHORT).show()
                player.losses++
            } else if (house.bust) {
                houseA.setImageResource(house.idList[0])
                player.newGame=true
                player.chips = (player.chips + bet) + (bet * 2)
                Toast.makeText(this, "House Bust! You Win", Toast.LENGTH_SHORT).show()
                player.wins++
            }
        }else if (player.bust && !player.newGame) {
            /**
             * player is currently bust
             * only the house hits
             */
            Toast.makeText(this, "Bust", Toast.LENGTH_SHORT).show()
            
            println("player hit and went over 21")
            //continue
            houseTurn(player, house, bet)

            //check hand value
            if (house.blackjack) {
                houseA.setImageResource(house.idList[0])
                player.newGame=true
                Toast.makeText(this, "Blackjack! House Win!", Toast.LENGTH_SHORT).show()

            } else if (house.bust) {
                houseA.setImageResource(house.idList[0])
                player.newGame=true
                Toast.makeText(this, "House Bust! No Winner", Toast.LENGTH_SHORT).show()
            }

        } else if(!player.newGame){
            /**
             * player
             */
            //check hand value
            if (player.blackjack) {
                player.chips = (player.chips + bet) + (bet * 2)
                player.newGame=true
                Toast.makeText(this, "Blackjack! You Win!", Toast.LENGTH_SHORT).show()
                player.wins++
            } else if (player.bust) {

                println("player hit and went over 21")
                //continue
                houseTurn(player, house, bet)
            }
        }
    }

    /**
     * handles the house's turn when
     * the player goes bust
     */
    fun houseTurn(player: Hand, house: House, bet: Int) {
        println("house's turn")
        println("player's score was " + player.score)
        val randomizer = CardRandomizer()
        var cardList: ArrayList<Int> = randomizer.getIDs(this)
        val rand = Random()
        player.newGame=false

        while (house.score < 17) {
            house.hit(rand, cardList)
            house.setStringListAfterHit(resources.getResourceEntryName(house.idList[house.index - 1]))

            if(house.index==3){
                houseC.setImageResource(house.idList[house.index-1])
            } else if(house.index==4){
                houseD.setImageResource(house.idList[house.index-1])
            } else if(house.index==5){
                houseE.setImageResource(house.idList[house.index-1])
            } else if(house.index==6){
                houseF.setImageResource(house.idList[house.index-1])
            } else if(player.index==7){
                houseG.setImageResource(house.idList[house.index-1])
            }
            scoreAfterHit(player, house, bet)
            if (house.index > 9) {
                break
            }
        }
        houseA.setImageResource(house.idList[0])
        //compare scores, the higher one wins
        if((player.bust && !house.bust) && !player.newGame){
            houseA.setImageResource(house.idList[0])
            player.newGame=true
            Toast.makeText(this, "House Wins", Toast.LENGTH_SHORT).show()
        }else if ((house.score > player.score && !house.bust)&& !player.newGame) {
            houseA.setImageResource(house.idList[0])
            player.newGame=true
            Toast.makeText(this, "House Wins", Toast.LENGTH_SHORT).show()
            player.losses++
            //third addition to losses
        } else if((house.bust && player.bust)&& !player.newGame){
            houseA.setImageResource(house.idList[0])
            player.newGame=true
            Toast.makeText(this, "House and Player Bust! No winner", Toast.LENGTH_SHORT).show()
        }
        else if ((house.score == player.score && !player.bust)&& !player.newGame) {
            houseA.setImageResource(house.idList[0])
            player.newGame=true
            Toast.makeText(this, "Tie! Player Wins", Toast.LENGTH_SHORT).show()
            player.chips = (player.chips + bet) + (bet * 2)
            player.wins++

        } else if ((player.score > house.score && !player.bust)&& !player.newGame) {
            houseA.setImageResource(house.idList[0])
            player.newGame=true
            Toast.makeText(this, "Player Wins", Toast.LENGTH_SHORT).show()
            player.chips = (player.chips + bet) + (bet * 2)
            player.wins++
        }

    }
}




