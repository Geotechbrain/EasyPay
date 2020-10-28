package com.geodeveloper.easypay.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.easypay.helper.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.IllegalStateException

class ProfileFragment : Fragment() {
    var userID = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        Utils.showLoader(context!!,"loading")
        loadUserDetails(view.profile_fragment_fullname,view.profile_fragment_email,view.profile_fragment_phoneNumber,view.profile_fragment_joinedDate,view.profile_fragment_username,view.profile_fragment_userID)
        view.profile_fragment_copy.setOnClickListener {
            val clipboard: ClipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("", userID)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context!!, "user ID copied", Toast.LENGTH_LONG).show()
        }

        return view
    }


    private fun loadUserDetails(profileFragmentFullname: TextView?, profileFragmentEmail: TextView?, profileFragmentPhonenumber: TextView?, profileFragmentJoineddate: TextView?, profileFragmentUsername: TextView, profileFragmentUserid: TextView) {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(UsersModel::class.java)
                    profileFragmentFullname!!.text = user!!.fullname
                    profileFragmentEmail!!.text = user.email
                    profileFragmentUsername.text = user.fullname
                    profileFragmentPhonenumber!!.text = user.phone_number
                    profileFragmentJoineddate!!.text = Utils.formatTime(user.reg_date!!.toLong())
                    profileFragmentUserid.text = user.user_id
                    userID = user.user_id!!
                    try {
                       Utils.dismissLoader()
                    }catch (e:IllegalStateException){}
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}