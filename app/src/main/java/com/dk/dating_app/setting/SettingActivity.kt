package com.dk.dating_app.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dk.dating_app.R
import com.dk.dating_app.auth.IntroActivity
import com.dk.dating_app.message.MyLikeListActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        // 1. 방법으로는 방금 봣듯이, 앱에서 코드로 notification 띄우기

        // 2. Firebase 콘솔에서 모든 앱에게 push 보내기

        // 3. 특정사용자에게 메세지 보내기(Firebase console에서)

        // 4. Firebase console이 아니라, 앱에서 직접 다른 사람에게 푸시메세지 보내기

        val mybtn = findViewById<Button>(R.id.myPageBtn)
        mybtn.setOnClickListener {

            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)

        }

        val myLikeBtn = findViewById<Button>(R.id.myLikeList)
        myLikeBtn.setOnClickListener {

            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)

        }

        val logoutBtn = findViewById<Button>(R.id.logOutBtn)
        logoutBtn.setOnClickListener {

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)

        }


    }
}