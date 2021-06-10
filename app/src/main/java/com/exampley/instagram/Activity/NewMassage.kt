package com.exampley.instagram.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.exampley.instagram.Adapter.UserItem
import com.exampley.instagram.R
import com.exampley.instagram.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_massage.*
import kotlinx.android.synthetic.main.user_row_new_massage.view.*

class NewMassage : AppCompatActivity() {
    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_massage)
        supportActionBar?.title = "Select User"
        fatchUser()
    }

    private fun fatchUser() {
        val db = FirebaseDatabase.getInstance().getReference("/users")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user!!))
                }

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    val userItem = item as UserItem
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                }


                recucler_newmassage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}




