package com.exampley.instagram.Adapter

import android.util.Log
import com.exampley.instagram.R
import com.exampley.instagram.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_massage.view.*

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_newMassage.text = user.displayName
        try {
            Picasso.get().load(user.imageUrl).into(viewHolder.itemView.userImage_newMassage)
            Log.d("Main", user.imageUrl)
        } catch (e: Throwable) {
            Log.d("Main", "$e")
        }

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_massage
    }
}