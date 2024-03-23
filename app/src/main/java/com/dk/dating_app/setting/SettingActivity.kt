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

        // 2. Firebase 콘솔에서 모든 앱에게 push 보내기

        // 3. 특정사용자에게 메시지 보내기 (Firebase 콘솔)

        // 4. Firebase console이 아니라 ,앱에서 직접 다른 사람에게 푸시메세지 보내기

        val myPageBtn = findViewById<Button>(R.id.myPageBtn)
        myPageBtn.setOnClickListener{
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val myLikeBtn = findViewById<Button>(R.id.myLikeList)
        myLikeBtn.setOnClickListener{
            val intent = Intent(this, MyLikeListActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logOutBtn)
        logoutBtn.setOnClickListener{

            val auth = Firebase.auth
            auth.signOut()

            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
    }
}