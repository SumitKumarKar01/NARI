package com.nari.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class FeedActivity : AppCompatActivity() {

    private lateinit var editTextPost: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val posts: MutableList<PostData> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize UI elements
        recyclerView = findViewById(R.id.item_post)

        // Set up RecyclerView and Adapter
        postAdapter = PostAdapter(posts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postAdapter

        // Fetch posts from Firestore
        fetchPostsFromFirestore()

        // Initialize UI elements
        editTextPost = findViewById(R.id.editTextPost)
        buttonSubmit = findViewById(R.id.buttonSubmit)




        // Set onClickListener for the "Post" button
        buttonSubmit.setOnClickListener {
            // Get the content of the post from the EditText
            val postContent = editTextPost.text.toString().trim()


            if (postContent.isNotEmpty()) {
                // Get the current user's ID from Firebase Auth
                val userId = auth.currentUser?.uid


                if (userId != null) {
                    // Generate a unique post ID
                    val postId = UUID.randomUUID().toString()


                    // Create a PostData object
                    val postData = PostData(
                        postId = postId,
                        userId = userId,
                        content = postContent,
                        upvotes = 0,
                        downvotes = 0,
                        comments = 0)

                    // Add the post to Firestore

                    addPostToFirestore(postData)
                }
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.info)

        bottomNavigationView.selectedItemId  = R.id.page_2
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.page_1  -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                    true
                }
                R.id.page_2 ->{

                    startActivity(Intent(this,FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.page_3 ->{
                    startActivity(Intent(this,info::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun addPostToFirestore(postData: PostData) {
        // Add the post to the "posts" collection in Firestore
        db.collection("posts")
            .document(postData.postId)
            .set(postData)
            .addOnSuccessListener {
                // Post added successfully
                editTextPost.text.clear() // Clear the EditText after posting
                showToast("Post added successfully")
            }
            .addOnFailureListener {
                // Handle failure\
                showToast("Failed to add post")
            }
    }

    private fun fetchPostsFromFirestore() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                posts.clear() // Clear the existing posts
                for (document in result) {
                    val post = document.toObject(PostData::class.java)
                    posts.add(post)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FeedActivity", "Error getting documents.", exception)
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}