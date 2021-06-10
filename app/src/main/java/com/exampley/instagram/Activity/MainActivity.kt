package com.exampley.instagram.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.exampley.instagram.Adapter.IPostAdapter
import com.exampley.instagram.Adapter.PostAdapter
import com.exampley.instagram.Adapter.SPostAdapter
import com.exampley.instagram.Dao.PostDao
import com.exampley.instagram.R
import com.exampley.instagram.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), IPostAdapter, NavigationView.OnNavigationItemSelectedListener,
    SPostAdapter {
    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingButton.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        //For recyclerView
        setUpRecyclerView()

        //For Navigation Bar
        setSupportActionBar(toolBar)
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            draw_layout,
            toolBar,
            R.string.Open_Drawer,
            R.string.Close_Drawer
        )
        draw_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_save_like)

        //For PP
        makeProfilePic()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions,this,this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    //For BackPress Button
    override fun onBackPressed() {
        if (draw_layout.isDrawerOpen(GravityCompat.START)) {
            draw_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun makeProfilePic() {
        GlobalScope.launch {

            val auth = FirebaseAuth.getInstance()
            val headerView: View? = nav_view.getHeaderView(0)

            try {
                headerView!!.findViewById<TextView>(R.id.nav_name).text =
                    auth.currentUser!!.displayName

                headerView.findViewById<TextView>(R.id.nav_email_id).text = auth.currentUser!!.email
            } catch (e: Throwable) {
                Log.d("TAG", "In makeProfile Method$e")

            }
            withContext(Dispatchers.Main) {
                try {
                    val img1: ImageView = headerView?.findViewById(R.id.nav_profile) as ImageView
                    Glide.with(applicationContext).load(auth.currentUser!!.photoUrl).circleCrop()
                        .into(img1)
                } catch (e: Throwable) {
                    Log.d("TAG", "In makeProfile Method For Image $e")
                }
            }


        }
    }



    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout ->
                logoutFun()
            R.id.nav_save_like ->
                startActivity(Intent(this, SavePost::class.java))
            R.id.nav_about ->
                startActivity(Intent(this, AboutMe::class.java))
        }
        draw_layout.closeDrawer(GravityCompat.START)
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Massage->
                try {
                    startActivity(Intent(this,LatestMassage::class.java))
                }
                catch (e:Throwable){
                    Log.d("ERROR",e.toString())
                }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu4,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //For Logout User
    private fun logoutFun() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, SIgn_In::class.java))
        finish()
    }

    override fun onSaveClicked(postId: String) {
        postDao.savePost(postId)
    }

    override fun onLikedClicked(postid: String) {
        postDao.updateLikes(postid)
    }

}