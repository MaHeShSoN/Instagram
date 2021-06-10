package com.exampley.instagram.Adapter

import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.exampley.instagram.R
import com.exampley.instagram.Utils
import com.exampley.instagram.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import kotlinx.android.synthetic.main.activity_save_post.*

class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener : IPostAdapter,private val listener_save: SPostAdapter) : FirestoreRecyclerAdapter<Post,PostAdapter.PostViewHolder>(
    options
) {
    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val postSave: ImageView = itemView.findViewById(R.id.saveButton)
        val buttonViewOption: TextView = itemView.findViewById(R.id.textViewOptions)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))

        //For Like Button Click Handler
        viewHolder.likeButton.setOnClickListener {
            listener.onLikedClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        //For Save Button Click Handler
        viewHolder.postSave.setOnClickListener {
            listener_save.onSaveClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }


        return viewHolder
    }




    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        //For Document Reference
        val db = FirebaseFirestore.getInstance()
        val snapshot = snapshots.getSnapshot(holder.adapterPosition)
        val docIdRef = snapshot.id //This is Document Id
        val docRef = db.collection("posts").document(docIdRef)
        var valueForPostUid = ""

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid


        holder.postText.text = model.text
        holder.userText.text = model.createBy.displayName
        Glide.with(holder.userImage.context).load(model.createBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text =  Utils.getTimeAgo(model.createdAt)

        try {
            //For Finding
            val isLiked = model.likedBy.contains(currentUserId)
            val isSave = model.saveBy.contains(docIdRef)
            //For Like Button
            if (isLiked) {
                Log.d("TAG", "In LikeButton")
                holder.likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.likeButton.context,
                        R.drawable.ic_liked
                    )
                )
            } else {
                Log.d("TAG", "In UnlikeButton")
                holder.likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.likeButton.context,
                        R.drawable.ic_unliked
                    )
                )
            }

            //For Save Button
            if (isSave) {
                Log.d("TAG", "In SaveButton")
                holder.postSave.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.postSave.context,
                        R.drawable.ic_save
                    )
                )

            } else {
                Log.d("TAG", "In UnsavedButton")
                holder.postSave.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.postSave.context,
                        R.drawable.ic_unsave
                    )
                )
            }
        }
        catch (e: Throwable){
            Log.d("TAG", "In Like and Save Vector $e")
        }

        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.result
                if (document!!.exists()) {
                    val users =
                        document["createBy"] as Map<*, *>?
                    valueForPostUid = users?.get("uid").toString()
                }
            }
        }



        holder.buttonViewOption.setOnClickListener {

            val popupMenu = PopupMenu(holder.buttonViewOption.context,holder.buttonViewOption)
            popupMenu.inflate(R.menu.menu2)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(p0: MenuItem?) : Boolean{
                    return when(p0?.itemId){
                        R.id.EditPost->
                            editHandler()
                        R.id.deletePost->
                            deleteHandle()
                        R.id.reportPost->
                            reportHandler()
                        else->
                            false
                    }

                }


                private fun reportHandler(): Boolean {
                    Snackbar.make(holder.buttonViewOption,"This Post is Reported",Snackbar.LENGTH_LONG).show()
                    return true
                }

                private fun deleteHandle(): Boolean {
                    docRef.delete().addOnFailureListener {
                        Log.d("Fail","$it")
                    }
                    db.collection(currentUserId).document(docIdRef).delete().addOnFailureListener {
                        Log.d("Fail","$it")
                    }
                    return true
                }

                private fun editHandler(): Boolean {
                    val dialogPlus = DialogPlus.newDialog(holder.buttonViewOption.context)
                        .setContentHolder(ViewHolder(R.layout.activity_diaglog_content))
                        .setExpanded(true,1500)
                        .create()
                    val viewHolder = dialogPlus.holderView
                    val updatedText = viewHolder.findViewById<TextView>(R.id.updatedText)
                    updatedText.text = model.text
                    dialogPlus.show()

                    val updateButton = viewHolder.findViewById<Button>(R.id.updateButton)
                    updateButton.setOnClickListener {
                        val objMap : MutableMap<String, Any>?=mutableMapOf()
                        objMap!!["text"] = updatedText.text.toString()

                        docRef.update(objMap)
                        db.collection(currentUserId).document(docIdRef).update(objMap)
                        dialogPlus.dismiss()
                    }


                    return true
                }
            })

            if(currentUserId!=valueForPostUid){
                popupMenu.menu.findItem(R.id.deletePost).isVisible = false;
                popupMenu.menu.findItem(R.id.EditPost).isVisible = false;
            }
            else {
                popupMenu.menu.findItem(R.id.reportPost).isVisible = false;
            }
            popupMenu.show()
        }




    }
}

interface SPostAdapter {
    fun onSaveClicked(postId: String)
}
interface IPostAdapter{
    fun onLikedClicked(postid: String)
}