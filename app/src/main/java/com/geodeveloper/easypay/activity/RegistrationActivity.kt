package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.paybills.helper.Utils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.d_verifiy_account_dialogue.*
import java.lang.Exception

class RegistrationActivity : AppCompatActivity() {
    private var progess:ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        progess = ProgressDialog(this)
        Utils.databaseRef().child("test").setValue(true)

        register_btn.setOnClickListener {
            val fullname =  fullName_txtfield.text.toString()
            val email = email_txtfield.text.toString()
            val number = number_txtfield.text.toString()
            val password = passsword_txtfield.text.toString()
            val confirmPassword =  confirm_password_txtfield.text.toString()
            when{
                TextUtils.isEmpty(fullname) -> Toast.makeText(this,"fullname is required",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(email) -> Toast.makeText(this,"email is required",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(password) -> Toast.makeText(this,"password is required",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(number) -> Toast.makeText(this,"number is required",Toast.LENGTH_LONG).show()
                password.length <6 -> Toast.makeText(this,"enter 6 minimum character as password",Toast.LENGTH_LONG).show()
                password != confirmPassword -> Toast.makeText(this,"password not match",Toast.LENGTH_LONG).show()
                else -> {
                    showProgress("please wait")
                    createAccount(fullname,email,password,number)
                }
            }
        }
    }

    private fun createAccount(fullname: String, email: String, password: String, number: String) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnSuccessListener {
                        val regDate = System.currentTimeMillis().toString()
                        val userID = Utils.currentUserID()
                        val userDetailMap = HashMap<String, Any>()
                        userDetailMap["user_id"] = userID
                        userDetailMap["fullname"] = fullname
                        userDetailMap["email"] = email
                        userDetailMap["phone_number"] = number
                        userDetailMap["reg_date"] = regDate
                        userDetailMap["wallet_balance"] = "0.0"
                        try {
                            Utils.databaseRef().child(Constants.users).child(userID).setValue(userDetailMap).addOnSuccessListener { setUserInfo ->
                                FirebaseAuth.getInstance().signOut()
                                dismissProgress()
                                val mDialogueView = LayoutInflater.from(this).inflate(R.layout.d_verifiy_account_dialogue, null)
                                val mBuilder = AlertDialog.Builder(this).setView(mDialogueView)
                                val mAlertDualogue = mBuilder.show()
                                mAlertDualogue.verify_dialogue_ok.setOnClickListener {
                                    mAlertDualogue.dismiss()
                                    val intent =  Intent(this, LoginActivity::class.java)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                }
                                mAlertDualogue.setOnCancelListener {
                                    mAlertDualogue.dismiss()
                                    val intent =  Intent(this, LoginActivity::class.java)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                    Animatoo.animateSwipeLeft(this)
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                } else {
                    dismissProgress()
                    Toast.makeText(this,"Error occur",Toast.LENGTH_LONG).show()

                }
            }
        } catch (e: Exception) { }
    }
    private fun showProgress(title:String){
        if(!progess!!.isShowing){
            progess!!.setTitle(title)
            progess!!.setCancelable(false)
            progess!!.show()
        }
    }
    private fun dismissProgress(){
        if(progess!!.isShowing){
            progess!!.dismiss()
        }
    }
}