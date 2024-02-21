package com.nari.app

data class CommentData(
    val commentId: String = "",
    val postId: String  = "",
    val userId: String  = "",
    val content: String  = "",
    var upvotes: Int  = 0,
    var downvotes: Int  = 0,
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()

)
