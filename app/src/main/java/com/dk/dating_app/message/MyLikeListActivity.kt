package com.dk.dating_app.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.dk.dating_app.R
import com.dk.dating_app.R.*
import com.dk.dating_app.auth.UserDataModel
import com.dk.dating_app.message.fcm.NotiModel
import com.dk.dating_app.message.fcm.PushNotification
import com.dk.dating_app.message.fcm.RetrofitInstance
import com.dk.dating_app.utils.FirebaseAuthUtils
import com.dk.dating_app.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 내가 좋아요한 사람이 나를 좋아요 한 리스트
 * 
 */
class MyLikeListActivity : AppCompatActivity() {

    private val TAG = "MyLikeListActivity"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listViewAdapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_my_like_list)

        val userListView = findViewById<ListView>(id.userListView)

        listViewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listViewAdapter

        //내가 좋아요한 리스트
        getMyLikeList()
        
        //전체 유저 중 내가 좋아요한 사람들 가져와서 나와 매칭이 되었는지 확인
        userListView.setOnItemClickListener{ parents, view, position, id ->

            checkMatching(likeUserList[position].uid.toString())

            val notiModel = NotiModel("a", "b")
            val pushModel = PushNotification(notiModel, likeUserList[position].token.toString())

            testPush(pushModel)
        }
    }

    private fun checkMatching(otherUid : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 리스트 안에서 나의 UID가 있는지 확인하면됨
                if (dataSnapshot.children.count() == 0) {
                    Toast.makeText(this@MyLikeListActivity, "매칭 실패 \uD83D\uDE25", Toast.LENGTH_SHORT).show()
                } else{
                    for (dataModel in dataSnapshot.children) {

                        val likeUserKey = dataModel.key.toString()
                        if (likeUserKey == uid) {
                            Toast.makeText(this@MyLikeListActivity, "매칭 완료\uD83D\uDE04 지금 결과를 확인하세요!", Toast.LENGTH_SHORT).show()
                        }

                        else{
                            Toast.makeText(this@MyLikeListActivity, "매칭 실패 \uD83D\uDE25", Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)

    }

    //내가 좋아요한 사람과 나를 좋아요 한 사람들의 리스트를 받아야 한다
    // 내가 좋아요한 사람이 누구를 좋아요 했는지 알 수 있다.
    private fun getMyLikeList() {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 리스트 안에서 나의 UID가 있는지 확인하면됨
                for (dataModel in dataSnapshot.children) {
                    //내가 좋아요 한 사람들의 uid가 likeUserList에 들어 있다.
                    likeUserListUid.add(dataModel.key.toString())
                }
                //전체 유저 데이터
                getUserDataList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //유저 정보 바인딩
                for (dataModel in dataSnapshot.children) {

                    val user = dataModel.getValue(UserDataModel::class.java)

                    //전체 유저중 내가 좋아한 사람 정보만 add한다.
                    if (likeUserListUid.contains(user?.uid)) {
                        likeUserList.add(user!!)
                    }
                }
                listViewAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    //푸시 메세지 보내는 테스트
    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {

        RetrofitInstance.api.postNotification(notification)

    }
}

