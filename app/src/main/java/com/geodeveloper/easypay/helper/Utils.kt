package com.geodeveloper.paybills.helper

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog

object Utils {
    var loader: AlertDialog? = null


    fun currentUserID():String{
        return  FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun currentUser(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }
    fun databaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
    fun showLoader(context: Context, title: String) {
        loader = SpotsDialog.Builder()
            .setContext(context)
            .setMessage(title)
            .setCancelable(false)
            .build()
            .apply {
            }
        if(!loader!!.isShowing){
            try {
                loader!!.show()
            }catch (e:Exception){}
        }else{
            loader!!.cancel()
        }
    }
    fun dismissLoader(){
        if(loader!!.isShowing){
            Handler().postDelayed(object : Runnable {
                override fun run() {
                    try {
                        loader!!.cancel()
                    }catch (e:java.lang.Exception){}
                }
            }, 3000)
        }

    }
}