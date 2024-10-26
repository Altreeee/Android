package com.example.chatroomui_1155218605


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatroomui_1155218605.ui.theme.ChatroomUI_1155218605Theme

import retrofit2.Callback
import retrofit2.Response

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



// 定义数据模型
data class Chatroom(val id: Int, val name: String)
data class ChatroomResponse(val data: List<Chatroom>, val status: String)

// 定义 API 接口
interface ChatroomApi {
    @GET("/get_chatrooms")
    suspend fun getChatrooms(): ChatroomResponse // 使用 suspend 函数
}


// Retrofit instance
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.227:55722"

    val api: ChatroomApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatroomApi::class.java)
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatroomUI_1155218605Theme {
                // 在 Compose 中获取聊天室列表
                val chatrooms = remember { mutableStateOf(listOf<Chatroom>()) }

                // 使用 LaunchedEffect 发起网络请求
                LaunchedEffect(Unit) {
                    // 在 IO 线程中加载聊天室数据
                    try {
                        val rooms = fetchChatrooms()
                        chatrooms.value = rooms
                    } catch (e: Exception) {
                        Log.e("API Error", "Failed to fetch chatrooms", e)
                    }
                }

                // 显示主屏幕
                MainScreen(chatrooms = chatrooms.value, onButtonClick = { chatroomId ->
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    intent.putExtra("chatroomId", chatroomId)
                    startActivity(intent)
                })
            }
        }
    }

    // get chatroom
    private suspend fun fetchChatrooms(): List<Chatroom> {
        return withContext(Dispatchers.IO) {
            val api = RetrofitInstance.api
            val response = api.getChatrooms() // use suspend function
            if (response.status == "OK") {
                response.data
            } else {
                Log.e("API Response", "Failed: status is not OK")
                emptyList()
            }
        }
    }
}




@Composable
fun MainScreen(chatrooms: List<Chatroom>, onButtonClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "IEMS5722",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 16.dp, top = 30.dp)
            )
            Text(text = "Loaded ${chatrooms.size} chatrooms", fontSize = 16.sp)

        }

        HorizontalDivider(thickness = 3.dp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        // 动态生成聊天室按钮
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            chatrooms.forEach { chatroom ->
                Button(
                    onClick = { onButtonClick(chatroom.id) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(text = chatroom.name, fontSize = 20.sp)
                }
            }
        }
    }
}



