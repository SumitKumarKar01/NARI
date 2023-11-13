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
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        holder.postContent.text = post.content
        holder.tvUpvoteCount.text = post.upvotes.toString()
        holder.tvDownvoteCount.text = post.downvotes.toString()
        holder.tvCommentCount.text = post.comments.toString()




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
                val userId = auth.currentUser?.uid
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
                        downvotes = 0
                        )

                    // Save the new comment to Firestore

                    saveCommentToFirestore(newComment)
                    post.comments++
                    holder.tvCommentCount.text = post.comments.toString()
                    updateVoteCountOnFirestore(post.postId, "comments", post.comments)
                }

                // Clear the comment input field
                holder.editComment.text.clear()
            }
        }

        holder.btnUpvote.setOnClickListener {
            Log.d("PostAdapter", "updating vote count ")
            // Increment upvote count locally
            post.upvotes++
            // Update the UI
            holder.tvUpvoteCount.text = post.upvotes.toString()
            // Update upvote count on Firestore
            updateVoteCountOnFirestore(post.postId, "upvotes", post.upvotes)

            notifyDataSetChanged()
        }

        holder.btnDownvote.setOnClickListener {
            // Increment downvote count locally
            post.downvotes++
            // Update the UI
            holder.tvDownvoteCount.text = post.downvotes.toString()
            // Update downvote count on Firestore
            updateVoteCountOnFirestore(post.postId, "downvotes", post.downvotes)

            notifyDataSetChanged()
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
                // Notify the adapter that the dataset has changed
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("PostAdapter", "Error adding comment to Firestore", exception)
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
