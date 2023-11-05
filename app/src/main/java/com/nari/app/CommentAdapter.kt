package com.nari.app
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nari.app.R  // Replace with your actual package name
import com.nari.app.CommentData  // Replace with your actual package and data model

class CommentAdapter(private val comments: List<CommentData>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]

        holder.commentContent.text = comment.content
        // Handle upvotes and downvotes logic as needed
        holder.upvotesTextView.text = comment.upvotes.toString()
        holder.downvotesTextView.text = comment.downvotes.toString()
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentContent: TextView = itemView.findViewById(R.id.textCommentContent)
        val upvotesTextView: TextView = itemView.findViewById(R.id.tvcUpvoteCount)
        val downvotesTextView: TextView = itemView.findViewById(R.id.tvcDownvoteCount)
    }
}
