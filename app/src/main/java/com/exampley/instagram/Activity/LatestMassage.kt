package com.exampley.instagram.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.exampley.instagram.Adapter.LatestMassageRow
import com.exampley.instagram.R
import com.exampley.instagram.model.Chat
import com.exampley.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_massage.*


class LatestMassage : AppCompatActivity() {

    companion object{
        var currentUser : User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_massage)
        setSupportActionBar(toolbar_latest_massage)

        listnerForLatestMassage()



        recyclerView_latest_massage.adapter = adapter


        recyclerView_latest_massage.addItemDecoration(
            DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        )
        adapter.setOnItemClickListener { item, view ->

            val intent = Intent(this,ChatLogActivity::class.java)
            val row = item as LatestMassageRow
            Log.d("ERROR",row.chatPartnerUser.toString()+" "+NewMassage.USER_KEY)
            intent.putExtra(NewMassage.USER_KEY,row.chatPartnerUser)
            startActivity(intent)

        }

        featchCurrentUser()



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.NewMassage->
                try {
                    startActivity(Intent(this,NewMassage::class.java))
                }
                catch (e:Throwable){
                    Log.d("ERROR",e.toString())
                }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu3, menu)
        return super.onCreateOptionsMenu(menu)
    }

    val latestMassageMap = HashMap<String, Chat>()

    private fun refreshRecyclerVIewMessage() {
        adapter.clear()
        latestMassageMap.values.forEach{
            adapter.add(LatestMassageRow(it))
        }
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    private fun listnerForLatestMassage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-massage/$fromid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val charMessage = snapshot.getValue(Chat::class.java) ?: return
                latestMassageMap[snapshot.key!!] = charMessage
                refreshRecyclerVIewMessage()
//                adapter.add(LatestMassageRow(charMessage))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val charMessage = snapshot.getValue(Chat::class.java) ?: return
                latestMassageMap[snapshot.key!!] = charMessage
                refreshRecyclerVIewMessage()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR",error.toString())
            }

        })
    }




    private fun featchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("Latest","CurrentUsser  ${currentUser?.displayName}")

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}