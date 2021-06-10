package com.exampley.instagram.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.exampley.instagram.Dao.PostDao
import com.exampley.instagram.R
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao : PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postDao = PostDao()

        postButton.setOnClickListener{
            val input = postInput.text.toString().trim()
            if(input.isNotEmpty()){
                postDao.addPost(input)
                finish()
            }
        }
    }
}