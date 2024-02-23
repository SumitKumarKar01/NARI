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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
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

    private var lastVisible: DocumentSnapshot? = null



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
        //fetchPostsFromFirestore()
        loadInitialPosts()
        listenForNewPosts()

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
                        comments = 0,
                        timestamp = com.google.firebase.Timestamp.now()
                    )

                    // Add the post to Firestore

                    addPostToFirestore(postData)
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 4. When the user scrolls to the last item, load the next 5 posts starting after the last visible post.
                if (!recyclerView.canScrollVertically(1)) {
                    loadMorePosts()
                }
            }
        })

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

    private fun loadInitialPosts() {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    lastVisible = documents.documents[documents.size() - 1]
                    for (document in documents) {
                        val post = document.toObject(PostData::class.java)
                        posts.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun loadMorePosts() {
        if (lastVisible != null) {
            db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible!!)
                .limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        lastVisible = documents.documents[documents.size() - 1]
                        for (document in documents) {
                            val post = document.toObject(PostData::class.java)
                            posts.add(post)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
        }
    }

    private fun listenForNewPosts() {
        val currentTime = com.google.firebase.Timestamp.now()

        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereGreaterThan("timestamp", currentTime)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("FeedActivity", "listen:error", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val post = dc.document.toObject(PostData::class.java)
                        posts.add(0, post)
                        postAdapter.notifyItemInserted(0)
                    }
                }
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}