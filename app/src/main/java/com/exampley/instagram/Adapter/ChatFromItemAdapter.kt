package com.exampley.instagram.Adapter

import com.exampley.instagram.R
import com.exampley.instagram.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from.view.*

class ChatFromItemAdapter(val text: String,val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from.text = text
        val uri = user.imageUrl
        Picasso.get().load(uri).into(viewHolder.itemView.otherImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }
}