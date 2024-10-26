package com.example.chatroomui_1155218605

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("get_messages")
    suspend fun getMessages(@Query("chatroom_id") chatroomId: Int): ApiResponse

    @POST("send_message")
    suspend fun sendMessage(@Body messageData: MessageData): ApiResponse
}
