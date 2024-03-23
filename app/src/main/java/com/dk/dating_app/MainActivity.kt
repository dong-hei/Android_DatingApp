package com.dk.dating_app

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.dk.dating_app.auth.UserDataModel
import com.dk.dating_app.setting.SettingActivity
import com.dk.dating_app.slider.CardStackAdapter
import com.dk.dating_app.utils.FirebaseAuthUtils
import com.dk.dating_app.utils.FirebaseRef
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter

    lateinit var manager : CardStackLayoutManager

    private val usersDataList = mutableListOf<UserDataModel>()

    private val TAG = "MainActivity"

    private var userCount = 0

    private val uid = FirebaseAuthUtils.getUid()

    private lateinit var currentUserGender : String

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

        // 내가 좋아요한 사람이 좋아요한 사람의 리스트가 있어야한다. -> a [d e z]

        //나와 다른 성별의 유저를 받아야 한다. 1. 일단 나의 성별을 알고 2. 전체 유저중 나의 성별과 다른 사람을 가져온다.
        val setting = findViewById<ImageView>(R.id.settingIcon)
        setting.setOnClickListener{

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

            val cardStackView = findViewById<CardStackView>(R.id.cardStackView)
        var manager : CardStackLayoutManager = CardStackLayoutManager(baseContext, object : CardStackListener {
                override fun onCardDragging(direction: Direction?, ratio: Float) {
                }

                override fun onCardSwiped(direction: Direction?) {

                    if (direction == Direction.Right) {

                        userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                    }

                    if (direction == Direction.Left) {
                    }
                    userCount = ++userCount
                    if (userCount == usersDataList.count()) {
                        getUserDataList(currentUserGender)
                        Toast.makeText(this@MainActivity, "유저정보가 갱신되었습니다.", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onCardRewound() {
                }

                override fun onCardCanceled() {
                }

                override fun onCardAppeared(view: View?, position: Int) {
                }

                override fun onCardDisappeared(view: View?, position: Int) {
                }

            }) // 뷰를 어떻게 사용할것이냐.
            cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
            cardStackView.layoutManager = manager
            cardStackView.adapter = cardStackAdapter

//        getUserDataList()
        getMyUserData()
        }

    private fun getMyUserData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val data = dataSnapshot.getValue(UserDataModel::class.java)

                currentUserGender = data?.gender.toString()

                getUserDataList(currentUserGender)

                }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(currentUserGender : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //유저 정보 바인딩
                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)

                    if (user!!.gender.toString().equals(currentUserGender)) {

                    } else{
                        usersDataList.add(user!!)
                    }
                }

                cardStackAdapter.notifyDataSetChanged() //어뎁터를 새롭게 동기화 시켜라
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    //유저의 좋아요를 표시하는 부분 : DB 값을 저장 (나의 uid, 좋아요한 사람의 uid)
    private fun userLikeOtherUser(myUid : String, otherUid : String){

        FirebaseRef.userLikeRef.child(myUid).child(otherUid).setValue("like!")

        getOtherUserLikeList(otherUid)
    }

    // 내가 좋아요한 사람이 누구를 좋아요 했는지 알 수 있다.
    private fun getOtherUserLikeList(otherUid: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 리스트 안에서 나의 UID가 있는지 확인하면됨
                for (dataModel in dataSnapshot.children) {

                    val likeUserKey = dataModel.key.toString()
                    if (likeUserKey.equals(uid)) {
                        Toast.makeText(this@MainActivity,"매칭 성공!", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                    }
                }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel( "Test_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        try {
            var builder = NotificationCompat.Builder(this, "Test_channel")
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle("매칭 성공!")
                .setContentText("❤매칭에 성공되었습니다.❤")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(this)){
                notify(123, builder.build())
            }
        } catch (e: SecurityException) {
        }
     }
    }