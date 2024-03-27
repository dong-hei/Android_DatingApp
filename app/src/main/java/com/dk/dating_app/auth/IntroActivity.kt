package com.dk.dating_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.dk.dating_app.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        val loginBtn : Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        val joinBtn : Button = findViewById(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)

        }

    }
}