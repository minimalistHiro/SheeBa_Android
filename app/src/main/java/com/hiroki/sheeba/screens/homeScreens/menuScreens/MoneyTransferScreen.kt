package com.hiroki.sheeba.screens.homeScreens.menuScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.ChatMessage
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.model.Friends
import com.hiroki.sheeba.model.RecentMessage
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDestructiveTextAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.notification
import com.hiroki.sheeba.util.FirebaseConstants.recentMessages
import com.hiroki.sheeba.util.FirebaseConstants.uid
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalMaterial3Api
@Composable
fun MoneyTransferScreen(viewModel: ViewModel, navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var approveUserUID: String by remember { mutableStateOf("") }   // 承認してきた相手ユーザーのUID

    // 最新メッセージ
    val _uiRMState = MutableStateFlow(listOf<RecentMessage>())
    val uiRMState: StateFlow<List<RecentMessage>> = _uiRMState.asStateFlow()
    val recentMessages by uiRMState.collectAsState()

    // 友達
    val _uiFRState = MutableStateFlow(listOf<Friends>())
    val uiFRState: StateFlow<List<Friends>> = _uiFRState.asStateFlow()
    val friends by uiFRState.collectAsState()

//    val listState = rememberLazyListState()
    var isShowApproveOrNotAlert = remember {
        mutableStateOf(false)
    }                                                                   // 友達追加か否か確認ダイアログの表示有無

    viewModel.recentMessages.clear()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        return
    }

    // 全最新メッセージを取得
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.recentMessages)
        .document(uid)
        .collection(FirebaseConstants.message)
        .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
        .addSnapshotListener { documents, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val recentMessages = mutableListOf<RecentMessage>()
            for (document in documents!!) {
                document.toObject(RecentMessage::class.java).let {
                    recentMessages.add(it)
                }
            }
            _uiRMState.value = recentMessages
        }

    // 全友達を取得
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.friends)
        .document(uid)
        .collection(FirebaseConstants.user)
        .orderBy(FirebaseConstants.username, Query.Direction.DESCENDING)
        .addSnapshotListener { documents, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val friends = mutableListOf<Friends>()
            for (document in documents!!) {
                document.toObject(Friends::class.java).let {
                    friends.add(it)
                }
            }
            _uiFRState.value = friends
        }

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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTopAppBar(
                    title = "",
                    color = Color.White,
                    onButtonClicked = {
                        navController.navigate(Setting.homeScreen)
                    }
                )

                // タブ
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    contentColor = Color.Black,
                    containerColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            color = Color.Black,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("トーク") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("友達") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                when (selectedTabIndex) {
                    0 -> {
                        // トークユーザー
                        LazyColumn {
                            item {
                                recentMessages.map {
                                    if (it != null) {
                                        CustomListRecentMessage(viewModel = viewModel, rm = it) {
                                            viewModel.currentUser.value?.uid?.let { uid ->
                                                viewModel.fetchUser(uid = if (uid == it.fromId ) it.toId else it.fromId)
                                                viewModel.updateRecentMessage(
                                                    document1 = uid,
                                                    document2 = if (uid == it.fromId) it.toId else it.fromId,
                                                    data = hashMapOf(
                                                        FirebaseConstants.isRead to true,
                                                    )
                                                )
                                            }
                                            navController.navigate(Setting.chatLogScreen)
                                        }
                                    }
                                    // 最後の行のみ空白を入れる
                                    if(recentMessages.last() == it) {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // 友達
                        LazyColumn {
                            item {
                                friends.map {
                                    if (it != null) {
                                        CustomListFriends(viewModel = viewModel, fr = it) {
                                            if (it.isApproval) {
                                                viewModel.fetchUser(uid = it.uid)
                                                navController.navigate(Setting.chatLogScreen)
                                            } else {
                                                if (it.approveUid == viewModel.currentUser.value?.uid) {
                                                    viewModel.handleError(title = "", text = "相手からのリクエスト許可を待っています。", exception = null)
                                                } else {
                                                    approveUserUID = it.uid
                                                    isShowApproveOrNotAlert.value = true
                                                }
                                            }
                                        }
                                    }
                                    // 最後の行のみ空白を入れる
                                    if(friends.last() == it) {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            viewModel.currentUser.value?.let {
                if (it.isStoreOwner) {
                    Column(verticalArrangement = Arrangement.Bottom) {
                        CustomCapsuleButton(text = "友達を追加",
                            onButtonClicked = {
                                viewModel.fetchFriends()
                                viewModel.fetchRecentMessages()
                                viewModel.fetchAllUserOtherThanSelf()
                                navController.navigate(Setting.createNewMessageScreen)
                            },
                            isEnabled = true,
                            color = Color.Blue)
                        Spacer(modifier = Modifier.height(100.dp))
                    }
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
        // 友達追加か否かの確認ダイアログ
        if(isShowApproveOrNotAlert.value) {
            CustomDestructiveTextAlertDialog(
                title = "このユーザーからのリクエストを承認しますか？",
                text = "リクエストを承認すると、メッセージ交換、送金が可能になります。",
                okText = "承認",
                destructiveText = "辞退",
                onOkButtonClicked = {
                    FirebaseAuth.getInstance().currentUser?.uid?.let {
                        val data = hashMapOf<String, Any>(
                            FirebaseConstants.isApproval to true,
                        )
                        // 自身と相手の情報を更新
                        viewModel.updateFriend(document1 = it, document2 = approveUserUID, data = data)
                        viewModel.updateFriend(document1 = approveUserUID, document2 = it, data = data)
                    }
                    isShowApproveOrNotAlert.value = false
                },
                onDestructiveButtonClicked = {
                    FirebaseAuth.getInstance().currentUser?.uid?.let {
                        // 自身と相手の情報を削除
                        viewModel.deleteFriend(document1 = it, document2 = approveUserUID)
                        viewModel.deleteFriend(document1 = approveUserUID, document2 = it)
                    }
                    isShowApproveOrNotAlert.value = false
                },
            )
        }
    }
}
@ExperimentalMaterial3Api
@Composable
fun CustomListRecentMessage(viewModel: ViewModel, rm: RecentMessage, onButtonClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .background(Color.White)
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        TextButton(
            onClick = {
                onButtonClicked.invoke()
            },
            colors = ButtonDefaults.textButtonColors(Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomImagePicker(
                    size = 60,
                    model = rm.profileImageUrl,
                    conditions = !rm.profileImageUrl.isNullOrEmpty(),
                    isAlpha = false
                ) {}

                Spacer(modifier = Modifier.width(20.dp))

                Column {

                    Text(
                        text = rm.username,
                        fontSize = with(LocalDensity.current) { (18 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )

                    Text(
                        text = if(rm.isSendPay) {
                            if(viewModel.currentUser.value?.uid == rm.fromId) {
                                "${rm.text}pt送りました"
                            } else {
                                "${rm.text}pt受け取りました"
                            }
                        } else {
                            rm.text
                        },
                        fontSize = with(LocalDensity.current) { (13 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // バッジ
                if(!rm.isRead) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomListFriends(viewModel: ViewModel, fr: Friends, onButtonClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .background(Color.White)
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        TextButton(
            onClick = {
                onButtonClicked.invoke()
            },
            colors = ButtonDefaults.textButtonColors(Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomImagePicker(
                    size = 60,
                    model = fr.profileImageUrl,
                    conditions = !fr.profileImageUrl.isNullOrEmpty(),
                    isAlpha = false
                ) {}

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = fr.username,
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Start,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!fr.isApproval) {
                    Icon(
                        modifier = Modifier
                            .widthIn(10.dp)
                            .heightIn(10.dp),
                        painter = painterResource(id = R.drawable.baseline_warning_amber_24),
                        contentDescription = "",
                        tint = Color.Red,
                    )
                }
            }
        }
    }
}
