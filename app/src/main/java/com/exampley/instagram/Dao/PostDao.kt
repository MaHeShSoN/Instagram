package com.exampley.instagram.Dao

import android.util.Log
import com.exampley.instagram.model.Post
import com.exampley.instagram.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    val postCollections : CollectionReference = db.collection("posts")
    private val auth = Firebase.auth

    fun addPost(text : String){

        GlobalScope.launch (Dispatchers.IO) {
            var currntUserId = auth.currentUser!!.uid
            val userDao: UserDao = UserDao()
            val user = userDao.getUserById(currntUserId).await().toObject(User::class.java)!!

            val currentTime = System.currentTimeMillis()
            val post = Post(text,user,currentTime)
            postCollections.document().set(post)
        }
    }

    fun getPostId(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }

    fun updateLikes(postId: String){
        GlobalScope.launch{
            var currentUserId = auth.currentUser!!.uid
            var post = getPostId(postId).await().toObject(Post::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if(isLiked) {
                post.likedBy.remove(currentUserId)
            } else {
                post.likedBy.add(currentUserId)
            }
            postCollections.document(postId).set(post)
        }
    }

    fun savePost(postId: String) {
        GlobalScope.launch {
            try {
                val currentUser = auth.currentUser?.uid
                val saveCollection: CollectionReference = db.collection(currentUser!!)
                val post = getPostId(postId).await().toObject(Post::class.java)!!
                val isSave = post.saveBy.contains(postId)


                if (isSave) {
                    post.saveBy.remove(postId)
                    saveCollection.document(postId).delete()
                } else {
                    post.saveBy.add(postId)
                    saveCollection.document(postId).set(post)
                }
                postCollections.document(postId).set(post)
            }
            catch (e: Throwable){
                Log.d("TAG","In savePost $e")
            }
        }
    }

}