package com.geodeveloper.easypay.fragment

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.customerly.Customerly
import kotlinx.android.synthetic.main.fragment_contact_us.*
import kotlinx.android.synthetic.main.fragment_contact_us.view.*
import java.lang.IllegalStateException

@Suppress("DEPRECATION")
class ContactUsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        setUserDetails()

        view.contact_us_send.setOnClickListener {
            val email = contact_us_email.text.toString()
            val fullname = contact_us_fullname.text.toString()
            val phoneNumber = contact_us_phone.text.toString()
            val title = contact_us_title.text.toString()
            val message = contact_us_message.text.toString()
            when {
                TextUtils.isEmpty(email) -> Toast.makeText(context!!, "enter your email", Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(fullname) -> Toast.makeText(context!!, "enter your fullname", Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(phoneNumber) -> Toast.makeText(context!!, "enter your number", Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(title) -> Toast.makeText(context!!, "enter your title", Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(message) -> Toast.makeText(context!!, "enter your message", Toast.LENGTH_LONG).show()
                else -> {
                   Utils.showLoader(context!!,"sending")
                    sendContactDetails(email,fullname,phoneNumber,title,message)
                }
            }
        }
        view.contact_us_livechat.setOnClickListener {
            Customerly.openSupport(activity!!)
        }
        return view
    }

    private fun sendContactDetails(email: String, fullname: String, phoneNumber: String, title: String, message: String) {
        val date = System.currentTimeMillis().toString()
        val contactID = Utils.databaseRef().push().key.toString()
        val map = HashMap<String,Any>()
        map["fullname"] = fullname
        map["number"] = phoneNumber
        map["title"] = title
        map["message"] = message
        map["email"] = email
        map["date"] = date
        map["user_id"] = Utils.currentUserID()
        map["id"] = contactID

        Utils.databaseRef().child(Constants.userContactMessage).child(contactID).setValue(map).addOnCompleteListener {
            if(it.isSuccessful){
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                    Utils.dismissLoader()
                        Toast.makeText(context,"we have recieved your message thanks",Toast.LENGTH_LONG).show()
                        contact_us_title.text!!.clear()
                        contact_us_message.text!!.clear()
                    }
                }, 3000)
            }else{
                Utils.dismissLoader()
                Toast.makeText(context,"error occur",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUserDetails() {
        try {
            Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val user = p0.getValue(UsersModel::class.java)
                        try {
                            contact_us_fullname.setText(user!!.fullname)
                        }catch (e:NullPointerException){}
                        try {
                            contact_us_email.setText(user!!.email)
                        }catch (e:NullPointerException){}
                        try {
                            contact_us_phone.setText(user!!.phone_number)
                        }catch (e:NullPointerException){}
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }catch (e:Exception){}

    }
}