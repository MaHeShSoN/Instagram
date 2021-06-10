package com.exampley.instagram.Dao

import android.util.Log
import com.exampley.instagram.model.User
import com.firebase.ui.firestore.paging.FirestoreDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db  = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val currentUser = FirebaseAuth.getInstance().uid.toString()
    private val dbRef = FirebaseDatabase.getInstance().getReference("/users/$currentUser")

    fun addUser(user: User?){
        try{
            user?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    usersCollection.document(user.uid).set(it)
                   try {
                       dbRef.setValue(it)
                   }catch (e:Throwable){
                       Log.d("ERROR",e.toString())
                   }
                }
            }
        }
        catch (e:Throwable){
            Log.d("ERROR",e.toString())
        }
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return usersCollection.document(uId).get()
    }
}