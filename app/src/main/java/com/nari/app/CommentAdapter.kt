package com.nari.app
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.nari.app.R  // Replace with your actual package name
import com.nari.app.CommentData  // Replace with your actual package and data model

class CommentAdapter(private val postId: String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private val comments: MutableList<CommentData> = mutableListOf()


    init {
        // Fetch comments for the given postId
        getCommentsForPost()
    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]

        holder.commentContent.text = comment.content
        // Handle upvotes and downvotes logic as needed
        holder.CmntupvotesTextView.text = comment.upvotes.toString()
        holder.CmntdownvotesTextView.text = comment.downvotes.toString()

        holder.btnCmntUpvote.setOnClickListener {
            Log.d("PostAdapter", "updating vote count ")
            // Increment upvote count locally
            comment.upvotes++
            // Update the UI
            holder.CmntupvotesTextView.text = comment.upvotes.toString()
            // Update upvote count on Firestore
            updateVoteCountOnFirestore(comment.commentId, "upvotes", comment.upvotes)

            notifyDataSetChanged()
        }

        holder.btnCmntDownvote.setOnClickListener {
            // Increment downvote count locally
            comment.downvotes++
            // Update the UI
            holder.CmntdownvotesTextView.text = comment.downvotes.toString()
            // Update downvote count on Firestore
            updateVoteCountOnFirestore(comment.commentId, "downvotes", comment.downvotes)

            notifyDataSetChanged()
        }

    }

    private fun getCommentsForPost() {
        val db = FirebaseFirestore.getInstance()
        val commentsCollection = db.collection("comments")

        commentsCollection
            .whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { result ->
                comments.clear()
                for (document in result) {
                    val comment = document.toObject(CommentData::class.java)
                    comments.add(comment)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("CommentAdapter", "Error getting comments for post $postId", exception)
            }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    private fun updateVoteCountOnFirestore(commentID: String, voteType: String, newCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("comments").document(commentID)

        // Update the upvote or downvote count in Firestore
        postRef.update(voteType, newCount)
            .addOnSuccessListener {
                Log.d("CommentAdapter", "updating vote count on Firestore for post $commentID")
                // Successfully updated vote count on Firestore
            }
            .addOnFailureListener { exception ->
                Log.w("CommentAdapter", "Error updating vote count on Firestore for post $commentID", exception)
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentContent: TextView = itemView.findViewById(R.id.textCommentContent)
        val CmntupvotesTextView: TextView = itemView.findViewById(R.id.tvcUpvoteCount)
        val CmntdownvotesTextView: TextView = itemView.findViewById(R.id.tvcDownvoteCount)
        val btnCmntUpvote: ImageButton = itemView.findViewById(R.id.btnCommentUpvote)
        val btnCmntDownvote: ImageButton = itemView.findViewById(R.id.btnCommentDownvote)
    }
}
