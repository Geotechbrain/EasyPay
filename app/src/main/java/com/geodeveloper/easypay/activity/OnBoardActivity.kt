package com.geodeveloper.easypay.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.geodeveloper.easypay.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_on_board.*

class OnBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)
        isUserRegisteered()
        btnRegister.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
        btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun isUserRegisteered() {
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}