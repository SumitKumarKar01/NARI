package com.nari.app

data class PostData(
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    var upvotes: Int = 0,
    var downvotes: Int = 0,
    var comments: Int = 0
)

