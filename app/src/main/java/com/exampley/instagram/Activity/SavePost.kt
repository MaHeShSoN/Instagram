package com.exampley.instagram.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampley.instagram.Adapter.SavePostAdapter
import com.exampley.instagram.R
import com.exampley.instagram.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_save_post.*

class SavePost : AppCompatActivity() {


    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var saveAdapter: SavePostAdapter
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_post)

        firebaseFirestore = FirebaseFirestore.getInstance()

        //Query
        try{
            val query: Query = firebaseFirestore.collection(currentUser!!.uid)

            //Firestore RecyclerOption
            val recyclerViewOptions : FirestoreRecyclerOptions<Post> = FirestoreRecyclerOptions.
            Builder<Post>().setQuery(
                query,
                Post::class.java).build()

            query.addSnapshotListener { p0, _ ->
                if (p0 != null) {

                    if(p0.size() > 0) {
                        empty_view.visibility = View.GONE;
                        recycler_save .visibility = View.VISIBLE
                    }else {
                        empty_view.visibility = View.VISIBLE;
                        recycler_save .visibility = View.GONE
                    }
                }
            }


            saveAdapter = SavePostAdapter(recyclerViewOptions)



            recycler_save.setHasFixedSize(true)

            recycler_save.adapter = saveAdapter


            recycler_save.layoutManager = LinearLayoutManager(this)

        }
        catch(e:Throwable){
            Log.d("TAG","In SavePost OnCreate Method")
        }

    }



    override fun onStart() {
        super.onStart()
        try{
            saveAdapter.startListening()
        }
        catch(e:Throwable){
            Log.d("TAG","In onStart SavePost $e")
        }
    }


    override fun onStop() {
        super.onStop()
        try{
            saveAdapter.stopListening()

        }
        catch(e:Throwable){
            Log.d("TAG","In onStop SavePost $e")

        }
    }

}

