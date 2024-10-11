package com.hiroki.sheeba.screens.homeScreens.menuScreens

import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.model.ChatMessage
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.fromId
import com.hiroki.sheeba.util.FirebaseConstants.toId
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.util.Setting.maxChatTextCount
import com.hiroki.sheeba.viewModel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//class ChatLogViewModel(viewModel: ViewModel) : ViewModel() {
//    override var progress = mutableStateOf(false)
//
//    private val _uiState = MutableStateFlow(listOf<ChatMessage>())
//    val uiState: StateFlow<List<ChatMessage>> = _uiState.asStateFlow()
//    init {
//        Log.d("メッセージ", "")
//        viewModel.chatUser.value?.let {
//            progress.value = true
//            val chatMessages = mutableListOf<ChatMessage>()
//
//            val fromId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
//                return@let
//            }
//
//            FirebaseFirestore
//                .getInstance()
//                .collection(FirebaseConstants.messages)
//                .document(fromId)
//                .collection(it.uid)
//                .orderBy(FirebaseConstants.timestamp, Query.Direction.ASCENDING)
//                .addSnapshotListener { documents, e ->
//                    if(e != null) {
//                        return@addSnapshotListener
//                    }
//
//                    for(document in documents!!) {
//                        document.toObject(ChatMessage::class.java).let {
//                            chatMessages.add(it)
//                        }
//                    }
//                    // StateFlowの値を更新
//                    _uiState.value = chatMessages
//                }
//        }
//    }
//
//    /**
//     * 全メッセージを取得
//     *
//     * @param toId トーク相手UID
//     * @return なし
//     */
//    fun fetchMessages(toId: String) {
//        progress.value = true
//        val chatMessages = mutableListOf<ChatMessage>()
//
//        val fromId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
//            return
//        }
//
//        FirebaseFirestore
//            .getInstance()
//            .collection(FirebaseConstants.messages)
//            .document(fromId)
//            .collection(toId)
//            .orderBy(FirebaseConstants.timestamp, Query.Direction.ASCENDING)
//            .addSnapshotListener { documents, e ->
//                if(e != null) {
//                    return@addSnapshotListener
//                }
//
//                for(document in documents!!) {
//                    document.toObject(ChatMessage::class.java).let {
//                        chatMessages.add(it)
//                    }
//                }
//                // StateFlowの値を更新
//                _uiState.value = chatMessages
//            }
//            .addOnSuccessListener { documents ->
//                for(document in documents) {
//                    document.toObject(ChatMessage::class.java).let {
//                        chatMessages.add(it)
//                    }
//                }
//                _uiState.update {
//                    chatMessages
//                }
////                _uiState.value = chatMessages
//                progress.value = false
//            }
//            .addOnFailureListener { exception ->
//                Log.d("FAILED", Setting.failureFetchMessages)
//            }
//    }
//}

@ExperimentalMaterial3Api
@Composable
fun ChatLogScreen(viewModel: ViewModel, navController: NavHostController) {
//    val chatLogViewModel = ChatLogViewModel(viewModel = viewModel)
    val _uiState = MutableStateFlow(listOf<ChatMessage>())
    val uiState: StateFlow<List<ChatMessage>> = _uiState.asStateFlow()
    val chatMessages by uiState.collectAsState()                            // 全メッセージ
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val fromId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        viewModel.handleError(title = "", text = Setting.failureFetchUID, exception = null)
        return
    }
    // 全メッセージを取得
    viewModel.chatUser.value?.let {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.messages)
            .document(fromId)
            .collection(it.uid)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .addSnapshotListener { documents, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val chatMessages = mutableListOf<ChatMessage>()
                for (document in documents!!) {
                    document.toObject(ChatMessage::class.java).let {
                        chatMessages.add(it)
                    }
                }
                _uiState.value = chatMessages
                // 画面最下部までスクロール
//                scope.launch {
//                    delay(1000)
//                    listState.scrollToItem(listState.layoutInfo.totalItemsCount)
//                }
            }
    }

    // 一番下にスクロール
//    LaunchedEffect(Unit) {
//        listState.scrollToItem(listState.layoutInfo.visibleItemsInfo.last().index)
//    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.chatLogBackground)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTopAppBar(
                    title = viewModel.chatUser.value?.username?: "",
                    color = colorResource(id = R.color.chatLogBackground),
                    onButtonClicked = {
                        navController.navigate(Setting.moneyTransferScreen)
                    }
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    // メッセージ
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            // 上下逆にする
                            .graphicsLayer { rotationZ = 180f }
                    ) {
                        // 最下部のスペーサー（上下反転させている為、一番上に配置）
                        item {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(160.dp))
                            }
                        }

                        items(chatMessages) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp, vertical = 5.dp)
                                    .graphicsLayer {
                                        rotationZ = 180f // 各メッセージを元の向きに戻す
                                    }
                            ) {
                                if (it.isSendPay) {
                                    if (viewModel.currentUser.value?.uid == it.fromId) {
                                        ChatLogPointCom(viewModel = viewModel, point = it.text, isSelf = true)
                                    } else {
                                        ChatLogPointCom(viewModel = viewModel, point = it.text, isSelf = false)
                                    }
                                } else {
                                    if (viewModel.currentUser.value?.uid == it.fromId) {
                                        ChatLogTextCom(viewModel = viewModel, text = it.text, isSelf = true)
                                    } else {
                                        ChatLogTextCom(viewModel = viewModel, text = it.text, isSelf = false)
                                    }
                                }
//                                // 最後の行のみ空白を入れる
//                                if(chatMessages.last() == it) {
//                                    Spacer(modifier = Modifier.height(160.dp))
//                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(160.dp))

                    // チャットボタンバー
                    ChatButtonBar(viewModel = viewModel)
//                    Column {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .background(Color.Gray)
//                                .height(70.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                        ) {
//                            Spacer(modifier = Modifier.width(10.dp))
//
//                            // 送るボタン
//                            Button(
//                                modifier = Modifier
//                                    .width(80.dp)
//                                    .height(50.dp),
//                                colors = ButtonDefaults.textButtonColors(
//                                    containerColor = Color.Blue,
//                                    contentColor = Color.White,
//                                ),
//                                onClick = {
//                                    // フォーカスを外す
//                                    focusManager.clearFocus()
//                                    focus = false
//                                }) {
//                                Text(
//                                    text = "＄送る",
//                                    fontSize = 10.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.White
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            // テキストフィールド
//                            TextField(
//                                value = chatText,
//                                onValueChange = { value ->
//                                    if (value.length <= maxChatTextCount) {
//                                        chatText = value
//                                    } else {
//                                        chatText = value.take(maxChatTextCount)
//                                    }
//                                },
//                                modifier = Modifier
//                                    .width(250.dp)
//                                    .height(50.dp)
//                                    .focusRequester(focusRequester)
//                                    .onFocusChanged { focusState ->
//                                        focus = focusState.isFocused
//                                    },
//                                placeholder = {
//                                    Text(
//                                        if (focus) "メッセージを入力" else "Aa",
//                                        fontSize = 12.sp,
//                                    )
//                                },
//                                singleLine = false,
//                                colors = TextFieldDefaults.textFieldColors(
//                                    focusedIndicatorColor = Color.White,
//                                    unfocusedIndicatorColor = Color.White
//                                )
//                            )
//
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            // 送信ボタン
//                            IconButton(
//                                onClick = {
//                                    // フォーカスを外す
//                                    focusManager.clearFocus()
//                                    focus = false
//                                    // 送信処理を実行
//                                    viewModel.chatUser.value?.let {
//                                        viewModel.handleSend(
//                                            toId = it.uid,
//                                            chatText = chatText,
//                                            lastText = lastText,
//                                            isSendPay = false
//                                        )
//                                    }
//
//                                    lastText = chatText
//                                    chatText = ""
//                                }
//                            ) {
//                                Icon(
//                                    modifier = Modifier
//                                        .width(40.dp)
//                                        .height(40.dp),
//                                    painter = painterResource(id = R.drawable.baseline_near_me_24),
//                                    contentDescription = "",
//                                    tint = Color.Blue,
//                                )
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(80.dp))
//                    }
                }
            }
        }
        // インジケーター
        if(viewModel.progress.value) {
            CircularProgressIndicator()
        }
        // ダイアログ
        if(viewModel.isShowDialog.value) {
            CustomAlertDialog(
                title = viewModel.dialogTitle.value,
                text = viewModel.dialogText.value) {
                viewModel.isShowDialog.value = false
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChatLogTextCom(viewModel: ViewModel, text: String, isSelf: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if(isSelf) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // アイコン画像
        if (!isSelf) {
            Spacer(modifier = Modifier.width(15.dp))
            CustomImagePicker(
                size = 40,
                model = viewModel.chatUser.value?.profileImageUrl,
                isAlpha = false,
                conditions = (!viewModel.chatUser.value?.profileImageUrl.isNullOrEmpty())) {}
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    colorResource(id = if (isSelf) R.color.sheebaDarkGreen else R.color.sheebaYellow),
                    RoundedCornerShape(16.dp)
                ) // Dark Green color with rounded corners
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = if (isSelf) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChatLogPointCom(viewModel: ViewModel, point: String, isSelf: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if(isSelf) Arrangement.End else Arrangement.Start
    ) {
        // アイコン画像
        Column(
            modifier = Modifier
                .padding(start = 15.dp, top = 8.dp, bottom = 8.dp)
        ) {
            if (!isSelf) {
                CustomImagePicker(
                    size = 40,
                    model = viewModel.chatUser.value?.profileImageUrl,
                    isAlpha = false,
                    conditions = (!viewModel.chatUser.value?.profileImageUrl.isNullOrEmpty())) {}
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    colorResource(id = if (isSelf) R.color.sheebaDarkGreen else R.color.sheebaYellow),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 48.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if(isSelf) "送る" else "受け取る",
                color = if (isSelf) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = point,
                    color = if (isSelf) Color.White else Color.Black,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "pt",
                    color = if (isSelf) Color.White else Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChatButtonBar(viewModel: ViewModel) {
    var focus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var chatText by remember { mutableStateOf("") }             // ユーザーの入力テキスト
    var lastText by remember { mutableStateOf("") }             // 一時保存用テキスト

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            // 送るボタン
            Button(
                modifier = Modifier
                    .width(80.dp)
                    .height(50.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                ),
                onClick = {
                    // フォーカスを外す
                    focusManager.clearFocus()
                    focus = false
                    PostOfficeAppRouter.navigateTo(Screen.SendPayScreen)
                }) {
                Text(
                    text = "＄送る",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // テキストフィールド
            TextField(
                value = chatText,
                onValueChange = { value ->
                    if (value.length <= maxChatTextCount) {
                        chatText = value
                    } else {
                        chatText = value.take(maxChatTextCount)
                    }
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        focus = focusState.isFocused
                    },
                placeholder = {
                    Text(
                        if (focus) "メッセージを入力" else "Aa",
                        fontSize = 12.sp,
                    )
                },
                singleLine = false,
                maxLines = 20,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 送信ボタン
            IconButton(
                onClick = {
                    // フォーカスを外す
                    focusManager.clearFocus()
                    focus = false
                    // 送信処理を実行
                    viewModel.chatUser.value?.let {
                        viewModel.handleSend(
                            toId = it.uid,
                            chatText = chatText,
                            lastText = lastText,
                            isSendPay = false
                        )
                    }

                    lastText = chatText
                    chatText = ""
                }
            ) {
                Icon(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    painter = painterResource(id = R.drawable.baseline_near_me_24),
                    contentDescription = "",
                    tint = Color.Blue,
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

