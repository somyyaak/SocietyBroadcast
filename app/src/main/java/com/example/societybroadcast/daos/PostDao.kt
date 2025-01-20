package com.example.societybroadcast.daos
import com.example.societybroadcast.models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db=FirebaseFirestore.getInstance()
    val postC=db.collection("posts")
    val auth=Firebase.auth

    @OptIn(DelicateCoroutinesApi::class)
    fun addPost(text:String){
        GlobalScope.launch {
            val currentId =auth.currentUser!!.uid
            val userDao=UserDao()
            val user=userDao.getUserById(currentId).await().toObject(com.example.societybroadcast.models.User::class.java)!!
            val currentTime=System.currentTimeMillis()
            val post= Post(text,currentTime,user)
            postC.document().set(post)

        }
    }

    private fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postC.document(postId).get()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateLikes(postId: String){
        GlobalScope.launch{
            val currentId =auth.currentUser!!.uid
            val post=getPostById(postId).await().toObject(Post::class.java)!!
            val isLike=post.likedBy.contains(currentId)
            if(isLike){
                post.likedBy.remove(currentId)
            }
            else{
                post.likedBy.add(currentId)
            }
            postC.document(postId).set(post)
        }
    }

}