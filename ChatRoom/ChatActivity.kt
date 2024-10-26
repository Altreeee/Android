package com.example.chatroomui_1155218605

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.chatroomui_1155218605.ui.theme.ChatroomUI_1155218605Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.withContext

data class Message(
    val content: String,
    val timestamp: String,
    val backgroundResId: Int,
    val isUserMessage: Boolean,
    val senderName: String // 添加发送者名称字段
)

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 获取传递的 chatroomId
        val chatroomId = intent.getIntExtra("chatroomId", 2) // 默认值为 2

        setContent {
            ChatroomUI_1155218605Theme {
                ChatScreen(onBackClick = {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }, chatroomId = chatroomId)// 传递 chatroomId 到 ChatScreen
            }
        }
    }
}

@Composable
fun ChatScreen(onBackClick: () -> Unit, chatroomId: Int) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope() // 创建一个 CoroutineScope 用于启动协程

    // 使用 LaunchedEffect 进行异步加载消息
    LaunchedEffect(Unit) {
        loadMessages(messages, chatroomId) // 异步加载初始消息
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackClick) {
                Text(text = "back", fontSize = 20.sp)
            }

            // 刷新按钮，异步重新加载消息
            IconButton(onClick = {
                messages.clear() // 清空消息列表
                coroutineScope.launch {
                    loadMessages(messages, chatroomId) // 异步重新加载消息
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        HorizontalDivider(thickness = 3.dp, color = Color.Black)

        LazyColumn(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(messages) { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (msg.isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (msg.isUserMessage) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = msg.backgroundResId),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.matchParentSize()
                            )
                            Text(
                                text = msg.content,
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = msg.timestamp,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Bottom)
                        )

                        // 显示发送者名称，并根据发送者类型调整对齐方式
                        Text(
                            text = msg.senderName, // 显示发送者名称
                            fontSize = 14.sp,
                            color = Color.Blue, // 可以设置发送者名称的颜色
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (message.isNotBlank()) {
                            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("GMT+8")
                            }.format(Date())
                            val backgroundResourceId = R.drawable.chat_bubble_bg
                            messages.add(Message(message, currentTime, backgroundResourceId, isUserMessage = true, senderName = "me"))
                            message = ""
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("GMT+8")
                        }.format(Date())
                        val backgroundResourceId = R.drawable.chat_bubble_bg
                        messages.add(Message(message, currentTime, backgroundResourceId, isUserMessage = true, senderName = "me"))
                        message = ""
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Send",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}




// GET and update UI
suspend fun loadMessages(messages: SnapshotStateList<Message>, chatroomId: Int) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.227:55722/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    try {
        val response = apiService.getMessages(chatroomId)
        val backgroundResourceId = R.drawable.chat_bubble_bg

        // Use withContext to switch to the main thread to update the UI
        withContext(Dispatchers.Main) {
            response.data.messages.forEach { messageData ->
                messages.add(
                    Message(
                        content = messageData.message,
                        timestamp = messageData.message_time,
                        backgroundResId = backgroundResourceId,
                        isUserMessage = false, // User message flags
                        senderName = messageData.name
                    )
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace() // Handling errors
    }
}

