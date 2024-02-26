package com.nari.app
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentChange
import java.util.UUID

class CommentAdapter(private val postId: String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private val comments: MutableList<CommentData> = mutableListOf()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


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

        val userId = auth.currentUser?.uid
        if (userId != null) {
            updateVoteDrawables(comment.commentId, userId, holder)
        }

        holder.btnCmntUpvote.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                firestore.collection("CmntVotes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("commentId", comment.commentId)
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
                                    comment.downvotes--
                                    comment.upvotes++
                                    holder.CmntdownvotesTextView.text = comment.downvotes.toString()
                                    holder.CmntupvotesTextView.text = comment.upvotes.toString()
                                    updateVoteCountOnFirestore(comment.commentId, "downvotes", comment.downvotes)
                                    updateVoteCountOnFirestore(comment.commentId, "upvotes", comment.upvotes)
                                    document.reference.update("voteType", "upvote")
                                } else if (voteType == "upvote") {
                                    comment.upvotes--
                                    holder.CmntupvotesTextView.text = comment.upvotes.toString()
                                    updateVoteCountOnFirestore(comment.commentId, "upvotes", comment.upvotes)
                                    document.reference.delete()
                                }
                            } else {
                                comment.upvotes++
                                holder.CmntupvotesTextView.text = comment.upvotes.toString()
                                updateVoteCountOnFirestore(comment.commentId, "upvotes", comment.upvotes)
                                val voteId = UUID.randomUUID().toString()
                                val vote = mapOf("userId" to userId, "commentId" to comment.commentId, "voteType" to "upvote")
                                firestore.collection("CmntVotes").document(voteId).set(vote)
                            }
                        } else {
                            Log.w("CommentAdapter", "Error checking votes", task.exception)
                        }
                        updateVoteDrawables(comment.commentId, userId, holder)
                    }
            }
        }

        holder.btnCmntDownvote.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                firestore.collection("CmntVotes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("commentId", comment.commentId)
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
                                    comment.upvotes--
                                    comment.downvotes++
                                    holder.CmntupvotesTextView.text = comment.upvotes.toString()
                                    holder.CmntdownvotesTextView.text = comment.downvotes.toString()
                                    updateVoteCountOnFirestore(comment.commentId, "upvotes", comment.upvotes)
                                    updateVoteCountOnFirestore(comment.commentId, "downvotes", comment.downvotes)
                                    document.reference.update("voteType", "downvote")
                                } else if (voteType == "downvote") {
                                    comment.downvotes--
                                    holder.CmntdownvotesTextView.text = comment.downvotes.toString()
                                    updateVoteCountOnFirestore(comment.commentId, "downvotes", comment.downvotes)
                                    document.reference.delete()
                                }
                            } else {
                                comment.downvotes++
                                holder.CmntdownvotesTextView.text = comment.downvotes.toString()
                                updateVoteCountOnFirestore(comment.commentId, "downvotes", comment.downvotes)
                                val voteId = UUID.randomUUID().toString()
                                val vote = mapOf("userId" to userId, "commentId" to comment.commentId, "voteType" to "downvote")
                                firestore.collection("CmntVotes").document(voteId).set(vote)
                            }
                        } else {
                            Log.w("CommentAdapter", "Error checking votes", task.exception)
                        }
                        updateVoteDrawables(comment.commentId, userId, holder)
                    }
            }
        }

    }

    private fun getCommentsForPost() {
        firestore.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("CommentAdapter", "listen:error", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    val comment = dc.document.toObject(CommentData::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            comments.add(comment)
                            notifyItemInserted(comments.size - 1)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val index = comments.indexOfFirst { it.commentId == comment.commentId }
                            if (index != -1) {
                                comments[index] = comment
                                notifyItemChanged(index)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            val index = comments.indexOfFirst { it.commentId == comment.commentId }
                            if (index != -1) {
                                comments.removeAt(index)
                                notifyItemRemoved(index)
                            }
                        }
                    }
                }
            }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    private fun updateVoteCountOnFirestore(commentId: String, voteType: String, newCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val commentRef = db.collection("comments").document(commentId)

        commentRef.update(voteType, newCount)
            .addOnSuccessListener {
                Log.d("CommentAdapter", "updating vote count on Firestore for comment $commentId")
            }
            .addOnFailureListener { exception ->
                Log.w("CommentAdapter", "Error updating vote count on Firestore for comment $commentId", exception)
            }
    }

    private fun updateVoteDrawables(commentId: String, userId: String, holder: ViewHolder) {
        firestore.collection("CmntVotes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("commentId", commentId)
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
                            holder.btnCmntUpvote.setImageResource(R.drawable.ic_upvote_icon)
                            holder.btnCmntDownvote.setImageResource(R.drawable.ic_downvote_icon_unselect)
                        } else if (voteType == "downvote") {
                            holder.btnCmntDownvote.setImageResource(R.drawable.ic_downvote_icon)
                            holder.btnCmntUpvote.setImageResource(R.drawable.ic_upvote_icon_unselect)
                        }
                    } else {
                        holder.btnCmntUpvote.setImageResource(R.drawable.ic_upvote_icon_unselect)
                        holder.btnCmntDownvote.setImageResource(R.drawable.ic_downvote_icon_unselect)
                    }
                } else {
                    Log.w("CommentAdapter", "Error checking votes", task.exception)
                }
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
