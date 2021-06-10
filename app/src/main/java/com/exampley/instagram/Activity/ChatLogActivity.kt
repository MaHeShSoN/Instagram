package com.exampley.instagram.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.exampley.instagram.R
import com.exampley.instagram.model.Chat
import com.exampley.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser :User?=  null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra(NewMassage.USER_KEY)

        toolbar_ChatLog?.title = toUser!!.displayName
        recyclerView_chatLog.adapter = adapter


        listnerForMassage()

        send_button.setOnClickListener {
            Log.d(TAG, "Attemp to send massage")
            performSendButton()
        }

    }

    private fun listnerForMassage() {
        val fromid = FirebaseAuth.getInstance().uid.toString()
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-message/$fromid/$toId")


        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMassage = snapshot.getValue(Chat()::class.java)
                Log.d(TAG, chatMassage?.text!!)
                if(chatMassage.fromId == FirebaseAuth.getInstance().uid){
                    try{
                        val currentUser = LatestMassage.currentUser
                        adapter.add(ChatFromItem(chatMassage.text,currentUser!!))
                    }catch (e:Throwable){
                        Log.d("ERROR", "Error is $e")
                    }

                }
                else{

                    adapter.add(ChatToItem(chatMassage.text,toUser!!))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }




    private fun performSendButton() {
        val massageText = edit_text_chat_log.text.toString()
        val fromid = FirebaseAuth.getInstance().uid.toString()
        val user = intent.getParcelableExtra<User>(NewMassage.USER_KEY)
        val toId = user!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-message/$fromid/$toId").push()
        val Fromref = FirebaseDatabase.getInstance().getReference("/user-message/$toId/$fromid").push()
        val massage = Chat(
            ref.key.toString(),
            massageText,
            fromid,
            toId,
            System.currentTimeMillis() / 1000
        )
        ref.setValue(massage).addOnSuccessListener {
            edit_text_chat_log.text.clear()
            recyclerView_chatLog.scrollToPosition(adapter.itemCount-1)
        }
        Fromref.setValue(massage)

        val latestrMassage = FirebaseDatabase.getInstance().getReference("/latest-massage/$fromid/$toId")
        latestrMassage.setValue(massage)

        val latestrMassage1 = FirebaseDatabase.getInstance().getReference("/latest-massage/$toId/$fromid")
        latestrMassage1.setValue(massage)



    }

}

class ChatFromItem(val text: String,val user:User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from.text = text
        val uri = user.imageUrl
        Picasso.get().load(uri).into(viewHolder.itemView.otherImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }
}

class ChatToItem(val text: String,val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to.text = text
        val uri = user.imageUrl
        Picasso.get().load(uri).into(viewHolder.itemView.myImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to
    }
}