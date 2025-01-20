package com.example.societybroadcast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.example.societybroadcast.daos.PostDao

class BroadcastActivity : AppCompatActivity() {
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postDao=PostDao()
        val postButton = findViewById<Button>(R.id.postButton)
        val postInput =  findViewById<EditText>(R.id.postInput)
        postButton.setOnClickListener{
            val input=postInput.text.toString().trim()
            if(input.isNotEmpty()){
                postDao.addPost(input)
                finish()
            }
        }
        }
    }
