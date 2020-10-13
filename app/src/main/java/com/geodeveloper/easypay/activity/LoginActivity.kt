package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.d_verifiy_account_dialogue.*

class LoginActivity : AppCompatActivity() {
    private var email:String? = null
    private var progess: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progess = ProgressDialog(this)

        email =  intent.getStringExtra("email")
        if(email!= null){
            login_email.setText(email)
        }

        next_button.setOnClickListener {
            val email = login_email.text.toString()
            val password = password_edit_text.text.toString()
            when{
                TextUtils.isEmpty(email) -> Toast.makeText(this,"email is required",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(password) -> Toast.makeText(this,"password is required",Toast.LENGTH_LONG).show()
                else -> {
                    showProgress("please wait")
                    loginUser(email,password)
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    FirebaseAuth.getInstance().signOut()
                    dismissProgress()
                    val mDialogueView = LayoutInflater.from(this).inflate(R.layout.d_verifiy_account_dialogue, null)
                    val mBuilder = AlertDialog.Builder(this).setView(mDialogueView)
                    val mAlertDualogue = mBuilder.show()
                    mAlertDualogue.verify_dialogue_ok.setOnClickListener {
                        mAlertDualogue.dismiss()
                    }
                }
            } else {
                dismissProgress()
                Toast.makeText(this, "invalid credentials provided", Toast.LENGTH_LONG).show()
            }
        }
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