package com.nari.app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class PostAdapter(private val posts: List<PostData>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]


        holder.postContent.text = post.content
        holder.tvUpvoteCount.text = post.upvotes.toString()
        holder.tvDownvoteCount.text = post.downvotes.toString()
        holder.tvCommentCount.text = post.comments.toString()


        if (userId != null) {
            updateVoteDrawables(post.postId, userId, holder)
        }



        // Set up comments RecyclerView
        val commentAdapter = CommentAdapter(post.postId)
        holder.commentsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.commentsRecyclerView.adapter = commentAdapter


        // Toggle comments visibility
        // Toggle comments visibility
        holder.btnComment.setOnClickListener {
            if (holder.commentsRecyclerView.visibility == View.VISIBLE) {
                holder.commentsRecyclerView.visibility = View.GONE
            } else {
                holder.commentsRecyclerView.visibility = View.VISIBLE
            }
        }
        // Add comment functionality
        holder.btnPostComment.setOnClickListener {
            val commentContent = holder.editComment.text.toString()
            if (commentContent.isNotEmpty()) {
                if (userId != null) {
                    // Generate a unique comment ID
                    val commentId = UUID.randomUUID().toString()


                    // Create a PostData object
                    val newComment = CommentData(
                        commentId = commentId,
                        postId = post.postId,
                        userId = userId,
                        content = commentContent,
                        upvotes = 0,
                        downvotes = 0,
                        timestamp = com.google.firebase.Timestamp.now()
                        )

                    // Save the new comment to Firestore

                    saveCommentToFirestore(newComment)
                    post.comments++
                    holder.tvCommentCount.text = post.comments.toString()
                    updateVoteCountOnFirestore(post.postId, "comments", post.comments)
                }

                // Clear the comment input field
                holder.editComment.text.clear()

                //holder.commentsRecyclerView.visibility = View.VISIBLE



            }
        }

        holder.btnUpvote.setOnClickListener {
            if (userId != null) {
                firestore.collection("postVotes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("postId", post.postId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val documents = task.result?.documents
                            val document = if (!documents.isNullOrEmpty()) {
                                documents[0]
                            } else {
                                null
                            }
                            if (document != null) {
                                val voteType = document.getString("voteType")
                                if (voteType == "downvote") {
                                    // The user has already downvoted this post, so decrement the downvote count, increment the upvote count and update the vote type to "upvote"
                                    post.downvotes--
                                    post.upvotes++
                                    holder.tvDownvoteCount.text = post.downvotes.toString()
                                    holder.tvUpvoteCount.text = post.upvotes.toString()
                                    updateVoteCountOnFirestore(post.postId, "downvotes", post.downvotes)
                                    updateVoteCountOnFirestore(post.postId, "upvotes", post.upvotes)
                                    document.reference.update("voteType", "upvote")
                                }
                                if (voteType == "upvote"){
                                    // The user has already upvoted this post, so decrement the upvote count and update the vote type to "none"
                                    post.upvotes--
                                    holder.tvUpvoteCount.text = post.upvotes.toString()
                                    updateVoteCountOnFirestore(post.postId, "upvotes", post.upvotes)
                                    document.reference.delete()
                                }

                            } else {
                                // The user hasn't voted on this post yet, so increment the upvote count and add a document to the votes collection with the vote type as "upvote"
                                post.upvotes++
                                holder.tvUpvoteCount.text = post.upvotes.toString()
                                updateVoteCountOnFirestore(post.postId, "upvotes", post.upvotes)
                                val voteId = UUID.randomUUID().toString()
                                val vote = mapOf("userId" to userId, "postId" to post.postId, "voteType" to "upvote")
                                firestore.collection("postVotes").document(voteId).set(vote)
                            }
                        } else {
                            Log.w("PostAdapter", "Error checking votes", task.exception)
                        }
                        updateVoteDrawables(post.postId, userId, holder)
                    }
            }
        }

        holder.btnDownvote.setOnClickListener {
            if (userId != null) {
                firestore.collection("postVotes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("postId", post.postId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val documents = task.result?.documents
                            val document = if (!documents.isNullOrEmpty()) {
                                documents[0]
                            } else {
                                null
                            }
                            if (document != null) {
                                val voteType = document.getString("voteType")
                                if (voteType == "upvote") {
                                    // The user has already upvoted this post, so decrement the upvote count, increment the downvote count and update the vote type to "downvote"
                                    post.upvotes--
                                    post.downvotes++
                                    holder.tvUpvoteCount.text = post.upvotes.toString()
                                    holder.tvDownvoteCount.text = post.downvotes.toString()
                                    updateVoteCountOnFirestore(post.postId, "upvotes", post.upvotes)
                                    updateVoteCountOnFirestore(post.postId, "downvotes", post.downvotes)
                                    document.reference.update("voteType", "downvote")
                                }
                                if (voteType == "downvote"){
                                    // The user has already downvoted this post, so decrement the downvote count and update the vote type to "none"
                                    post.downvotes--
                                    holder.tvDownvoteCount.text = post.downvotes.toString()
                                    updateVoteCountOnFirestore(post.postId, "downvotes", post.downvotes)
                                    document.reference.delete()
                                }

                            } else {
                                // The user hasn't voted on this post yet, so increment the downvote count and add a document to the votes collection with the vote type as "downvote"
                                post.downvotes++
                                holder.tvDownvoteCount.text = post.downvotes.toString()
                                updateVoteCountOnFirestore(post.postId, "downvotes", post.downvotes)
                                val voteId = UUID.randomUUID().toString()
                                val vote = mapOf("userId" to userId, "postId" to post.postId, "voteType" to "downvote")
                                firestore.collection("postVotes").document(voteId).set(vote)
                            }
                        } else {
                            Log.w("PostAdapter", "Error checking votes", task.exception)
                        }
                        updateVoteDrawables(post.postId, userId, holder)
                    }
            }
        }


    }

    override fun getItemCount(): Int {
        return posts.size
    }


    private fun updateVoteCountOnFirestore(postId: String, voteType: String, newCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("posts").document(postId)

        // Update the upvote or downvote count in Firestore
        postRef.update(voteType, newCount)
            .addOnSuccessListener {
                Log.d("PostAdapter", "updating vote count on Firestore for post $postId")
                // Successfully updated vote count on Firestore
            }
            .addOnFailureListener { exception ->
                Log.w("PostAdapter", "Error updating vote count on Firestore for post $postId", exception)
            }
    }
    private fun saveCommentToFirestore(commentData: CommentData) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .document(commentData.commentId)
            .set(commentData)
            .addOnSuccessListener {
                Log.d("PostAdapter", "Comment added to Firestore: ${commentData.commentId}")
                // Notify the adapter that an item has been inserted
                //notifyItemInserted(posts.size - 1)
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("PostAdapter", "Error adding comment to Firestore", exception)
            }
    }

    private fun updateVoteDrawables(postId: String, userId: String, holder: ViewHolder) {
    firestore.collection("postVotes")
        .whereEqualTo("userId", userId)
        .whereEqualTo("postId", postId)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result?.documents
                val document = if (!documents.isNullOrEmpty()) {
                    documents[0]
                } else {
                    null
                }
                if (document != null) {
                    val voteType = document.getString("voteType")
                    if (voteType == "upvote") {
                        holder.btnUpvote.setImageResource(R.drawable.ic_upvote_icon)
                        holder.btnDownvote.setImageResource(R.drawable.ic_downvote_icon_unselect)
                    } else if (voteType == "downvote") {
                        holder.btnDownvote.setImageResource(R.drawable.ic_downvote_icon)
                        holder.btnUpvote.setImageResource(R.drawable.ic_upvote_icon_unselect)
                    }
                } else {
                    holder.btnUpvote.setImageResource(R.drawable.ic_upvote_icon_unselect)
                    holder.btnDownvote.setImageResource(R.drawable.ic_downvote_icon_unselect)
                }
            } else {
                Log.w("PostAdapter", "Error checking votes", task.exception)
            }
        }
    }




    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postContent: TextView = itemView.findViewById(R.id.textPostContent)
        val btnUpvote: ImageButton = itemView.findViewById(R.id.btnUpvote)
        val btnDownvote: ImageButton = itemView.findViewById(R.id.btnDownvote)
        val btnComment: ImageButton = itemView.findViewById(R.id.btnComment)
        val tvUpvoteCount: TextView = itemView.findViewById(R.id.tvUpvoteCount)
        val tvDownvoteCount: TextView = itemView.findViewById(R.id.tvDownvoteCount)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
//        val commentingBox: LinearLayout = itemView.findViewById(R.id.commentingBox)
        val editComment: EditText = itemView.findViewById(R.id.editComment)
        val btnPostComment: ImageButton = itemView.findViewById(R.id.btnPostComment)
        val commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.commentsRecyclerView)
    }
}
