package com.exampley.instagram.Adapter



import com.exampley.instagram.R
import com.exampley.instagram.model.Chat
import com.exampley.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_massage_row.view.*

class LatestMassageRow(val chatMassage: Chat): Item<GroupieViewHolder>(){

    var chatPartnerUser : User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.userlastmassage_latestMassageRow.text =  chatMassage.text
        val chatpatnerId : String
        if(chatMassage.fromId == FirebaseAuth.getInstance().uid){
            chatpatnerId = chatMassage.toId
        }
        else{
            chatpatnerId = chatMassage.fromId
        }
        val username = FirebaseDatabase.getInstance().getReference("/users/$chatpatnerId").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                chatPartnerUser = user
                viewHolder.itemView.username_latestMassageRow.text = user!!.displayName
                Picasso.get().load(user?.imageUrl).into(viewHolder.itemView.userImage_latestMassageRow)
            }
        })


    }

    override fun getLayout(): Int {
        return R.layout.latest_massage_row
    }

}