package com.example.chatroomui_1155218605

data class ApiResponse(
    val data: Data
)

data class Data(
    val messages: List<MessageData>
)

data class MessageData(
    val message: String,
    val name: String,
    val message_time: String,
    val user_id: Int
)
