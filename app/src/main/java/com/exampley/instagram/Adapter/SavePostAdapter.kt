package com.exampley.instagram.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.exampley.instagram.R
import com.exampley.instagram.Utils
import com.exampley.instagram.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SavePostAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, SavePostAdapter.SaveViewHolder>(options) {

    //ViewHolder Class
    class SaveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val postText1: TextView = itemView.findViewById(R.id.postTitle)
        val userText1: TextView = itemView.findViewById(R.id.userName)
        val createdAt1: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount1: TextView = itemView.findViewById(R.id.likeCount)
        val userImage1: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton1: ImageView = itemView.findViewById(R.id.likeButton)
        val postSave1: ImageView = itemView.findViewById(R.id.saveButton)

    }

    //OnCreate Method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveViewHolder {


        return SaveViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_post,
            parent,
            false              ))
    }

    //OnBindMethod
    override fun onBindViewHolder(holder: SaveViewHolder, position: Int, model: Post) {
        try{
            //For Document Reference
            val snapshot = snapshots.getSnapshot(holder.adapterPosition)
            val countryName = snapshot.id
            val auth = Firebase.auth
            var currentUserId = auth.currentUser!!.uid

            //For Basic Post Structure
            holder.postText1.text = model.text
            holder.userText1.text = model.createBy.displayName
            Glide.with(holder.userImage1.context).load(model.createBy.imageUrl).circleCrop().into(holder.userImage1)
            holder.likeCount1.text = model.likedBy.size.toString()
            holder.createdAt1.text = Utils.getTimeAgo(model.createdAt)

            //For Checking Button was Already Clicked or Not
            val isLiked = model.likedBy.contains(currentUserId)
            val isSave = model.saveBy.contains(countryName)


            //For Like Button
            if (isLiked) {
                holder.likeButton1.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.likeButton1.context,
                        R.drawable.ic_liked
                    )
                )
            } else {
                holder.likeButton1.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.likeButton1.context,
                        R.drawable.ic_unliked
                    )
                )
            }

            //For Save Button
            if (isSave) {
                holder.postSave1.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.postSave1.context,
                        R.drawable.ic_save
                    )
                )

            } else {
                holder.postSave1.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.postSave1.context,
                        R.drawable.ic_unsave
                    )
                )
            }

        }catch (e:Throwable){
            Log.d("TAG",e.toString())
        }
    }
}