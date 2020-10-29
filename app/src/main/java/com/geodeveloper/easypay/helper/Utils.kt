package com.geodeveloper.easypay.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.widget.Toast
import com.github.marlonlom.utilities.timeago.TimeAgo
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

    fun getUserEmail(context: Context):String{
        val userEmail = context.getSharedPreferences("USERPREF", Context.MODE_PRIVATE).getString("useremail","")
        return userEmail!!
    }

    fun getUserName(context: Context):String{
        val userName = context.getSharedPreferences("USERPREF", Context.MODE_PRIVATE).getString("userfullname","")
        return userName!!
    }
    fun formatTime(timeInMilliseconds: Long): String {
        val formatedDate = TimeAgo.using(timeInMilliseconds)
        return formatedDate
    }
    fun copyValue(context: Activity, value:String){
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("", value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "copied", Toast.LENGTH_LONG).show()
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