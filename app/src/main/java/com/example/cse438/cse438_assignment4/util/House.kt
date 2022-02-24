package com.example.cse438.cse438_assignment4.util

import java.util.*
import kotlin.collections.ArrayList

class House {
    var idList = IntArray(10)
    var stringList: Array<String?> = arrayOfNulls(10)
    var index: Int = 0
    var score: Int = 0
    var newGame: Boolean = true
    var blackjack: Boolean = false
    var bust: Boolean = false

    /**
     * deals the first two cards of a round by setting
     * the first two indices of the "hand" array to a
     * random card, and clears all other values
     */
    fun newHand(rand: Random, cardList: ArrayList<Int>){
        index=0
        score=0
        blackjack=false
        bust=false
        newGame=false
        for(i in 2..9){
            idList[i] = 0
        }
        for(i in 0..1){
            val r: Int = rand.nextInt(cardList.size)
            val id: Int = cardList.get(r)
            idList[i] = id
            index++
        }
    }

    /**
     * inits hand's string array from the idList
     * e.g. [clubs2, diamonds8, spades_ace, null, null, ...]
     * whenever a card name is inserted in the list,
     * its value is added to the hand's score
     */
    fun setStringListAfterDeal(resource: String, i: Int){
        stringList[i] = resource
        score += scoreHelper(resource)
        checkBlackjack()
        checkBust()
    }

    /**
     * Hit: add another card in hand
     */
    fun hit(rand: Random, cardList: ArrayList<Int>){
        if(index > 9){
            println("Error: Out of bounds index")
        } else{
            val r: Int = rand.nextInt(cardList.size)
            val id: Int = cardList.get(r)
            idList[index] = id
            index++
        }
    }

    fun setStringListAfterHit(resource: String){
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        println("score added from index: " + index)
        stringList[index-1] = resource
        score += scoreHelper(resource)
        checkBlackjack()
        checkBust()
    }

    /**
     * evaluates amount of points in hand
     */
    fun setScore(){
        for(i in 0..(index-1)){
            //add card value to score
            score += scoreHelper(stringList[i])
        }
    }

    /**
     * check hand value for blackjack
     */
    fun checkBlackjack(): Boolean{
        if(score==21){
            blackjack=true
        }
        return blackjack
    }

    /**
     * check hand value for bust
     */
    fun checkBust(): Boolean{
        if(score>21){
            bust=true
        }
        return bust
    }

    /**
     * prints string of card names
     * used for debugging purposes
     */
    fun printHand(){
        for(i in 0..(index-1)){
            println(stringList[i])
        }
    }

    /**
     * takes in the card name and returns its associated point value
     */
    fun scoreHelper(card: String?): Int{
        if(card=="clubs2" || card=="diamonds2" || card=="hearts2" || card=="spades2"){ return 2 }
        if(card=="clubs3" || card=="diamonds3" || card=="hearts3" || card=="spades3"){ return 3 }
        if(card=="clubs4" || card=="diamonds4" || card=="hearts4" || card=="spades4"){ return 4 }
        if(card=="clubs5" || card=="diamonds5" || card=="hearts5" || card=="spades5"){ return 5 }
        if(card=="clubs6" || card=="diamonds6" || card=="hearts6" || card=="spades6"){ return 6 }
        if(card=="clubs7" || card=="diamonds7" || card=="hearts7" || card=="spades7"){ return 7 }
        if(card=="clubs8" || card=="diamonds8" || card=="hearts8" || card=="spades8"){ return 8 }
        if(card=="clubs9" || card=="diamonds9" || card=="hearts9" || card=="spades9"){ return 9 }
        if(card=="clubs10" || card=="diamonds10" || card=="hearts10" || card=="spades10"){ return 10 }
        if(card=="clubs_jack" || card=="diamonds_jack" || card=="hearts_jack" || card=="spades_jack"){ return 10 }
        if(card=="clubs_queen" || card=="diamonds_queen" || card=="hearts_queen" || card=="spades_queen"){ return 10 }
        if(card=="clubs_king" || card=="diamonds_king" || card=="hearts_king" || card=="spades_king"){ return 10 }
        if(card=="clubs_ace" || card=="diamonds_ace" || card=="hearts_ace" || card=="spades_ace"){
            if(score + 11 > 21){
                return 1
            } else return 11
        }
        return 1
    }
}