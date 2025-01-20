package com.example.societybroadcast.models

data class Post (
    val text : String="",
    val createdAt: Long =0L,
    val createdBy: User =User(),
    val likedBy: ArrayList<String> = ArrayList()
)