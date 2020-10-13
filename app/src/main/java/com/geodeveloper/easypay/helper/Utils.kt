package com.geodeveloper.paybills.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

object Utils {
    fun currentUserID():String{
        return  FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun currentUser(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }
    fun databaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
}