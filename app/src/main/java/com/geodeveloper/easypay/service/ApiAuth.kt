package com.geodeveloper.easypay.service

import android.util.Base64
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.model.AuthCred
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.UnsupportedEncodingException

object ApiAuth {
    var email:String? = null
    var password:String? = null

    @JvmStatic
    fun main(args: Array<String>) {
        getAuthCredentials()
    }

    fun getAuthCredentials(){
     Utils.databaseRef().child(Constants.apiAuth).child(Constants.test).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(AuthCred::class.java)
                email = data!!.email
                password = data.password
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    fun getAuthToken():String {
        var data = ByteArray(0)
        try {
            data = (email+ ":" + password).toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) { e.printStackTrace() }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
    }
}