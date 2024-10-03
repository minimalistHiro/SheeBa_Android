package com.hiroki.sheeba.screens.homeScreens.menuScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.model.Friends
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomDoubleAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.friends
import com.hiroki.sheeba.util.FirebaseConstants.recentMessages
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalMaterial3Api
@Composable
fun CreateNewMessageScreen(viewModel: ViewModel, navController: NavHostController) {
    val _uiState = MutableStateFlow(listOf<ChatUser>())
    val uiState: StateFlow<List<ChatUser>> = _uiState.asStateFlow()
    var active by rememberSaveable { mutableStateOf(false) }
    val resultUsers by uiState.collectAsState()                         // 検索結果に一致したユーザー情報
    var chatUser: ChatUser by remember { mutableStateOf(ChatUser()) }   // 選択したユーザーのユーザー情報
    var isShowAddFriendDialog = remember {
        mutableStateOf(false)
    }                                                                   // 友達追加確認ダイアログの表示有無
//    val friends = mutableListOf<Friends?>()                     // 友達情報
//    viewModel.progress.value = true

//    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
//        return
//    }

    // 全友達を取得
//    FirebaseFirestore
//        .getInstance()
//        .collection(FirebaseConstants.friends)
//        .document(uid)
//        .collection(FirebaseConstants.user)
//        .orderBy(FirebaseConstants.username, Query.Direction.DESCENDING)
//        .get()
//        .addOnSuccessListener { documents ->
//            for(document in documents) {
//                document.toObject(Friends::class.java).let {
//                    friends.add(it)
//                }
//            }
//            viewModel.progress.value = false
//        }
//        .addOnFailureListener { exception ->
//            viewModel.handleError(title = "", text = Setting.failureFetchFriends, exception = exception)
//        }


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
                    title = "友達を追加",
                    color = Color.White,
                    onButtonClicked = {
                        navController.navigate(Setting.moneyTransferScreen)
                    }
                )

                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CustomSearchBar(
                        hint = "ユーザー名を入力してください",
                        viewModel = viewModel,
                        uiState = _uiState)
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    // 友達
                    LazyColumn {
                        item {
                            resultUsers.map {
                                CustomListSearchedUsers(viewModel = viewModel, user = it) {
                                    chatUser = it
                                    isShowAddFriendDialog.value = true
                                }
                                // 最後の行のみ空白を入れる
                                if (resultUsers.last() == it) {
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                        }
                    }
                }

//                SearchBar(
//                    query = searchedText,
//                    onQueryChange = {
//                        val resultUsers = mutableListOf<ChatUser>()               // 検索結果に一致したユーザー情報
//
//                        if (it.isEmpty()) {
//                            _uiState.value = resultUsers
//                        } else if (recentMessages.isEmpty()) {
//                            // 最新メッセージが空の場合
//                            resultUsers.addAll(
//                                viewModel.allUsers.filter { user ->
//                                    // 全ユーザーの中で、既に友達申請しているユーザーを排除する
//                                    viewModel.friends.none { friend -> friend?.uid == user.uid } &&
//                                            // 検索ワードに一致するユーザー名のみ含める
//                                            user.username.contains(it, ignoreCase = true)
//                                }
//                            )
//                            _uiState.value = resultUsers
//                        } else {
//                            // 検索ワードに一致するユーザーをフィルタ
//                            val filteredUsers = viewModel.allUsers.filter { user ->
//                                user.username.contains(it, ignoreCase = true)
//                            }
//
//                            // 最近のメッセージのトーク相手のUIDを取得
//                            val recentMessageIds = viewModel.recentMessages.map { message ->
//                                if (viewModel.currentUser.value?.uid == message?.fromId) message?.toId else message?.fromId
//                            }
//
//                            // 最近メッセージをやり取りした相手を省く
//                            resultUsers.addAll(
//                                filteredUsers.filter { user ->
//                                    !recentMessageIds.contains(user.uid)
//                                }
//                            )
//                            _uiState.value = resultUsers
//                        }
//                    },
//                    onSearch = { active = false },
//                    active = active,
//                    onActiveChange = {
//                        active = it
//                    },
//                ) {
//                    // 友達
//                    LazyColumn {
//                        item {
//                            resultUsers.map {
//                                CustomListSearchedUsers(viewModel = viewModel, user = it) {
//
//                                }
//                            }
//                        }
//                    }
//                }
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
        // 友達追加確認ダイアログ
        if(isShowAddFriendDialog.value) {
            CustomDoubleAlertDialog(
                title = "",
                text = "友達追加リクエストを送信しますか？",
                okText = "リクエスト送信",
                onOkButtonClicked = {
                    viewModel.persistFriend(chatUser = chatUser, isApproval = false)
                    navController.navigate(Setting.moneyTransferScreen)
                },
                onCancelButtonClicked = {
                    isShowAddFriendDialog.value = false
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomListSearchedUsers(viewModel: ViewModel, user: ChatUser, onButtonClicked: () -> Unit) {
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
                    model = user.profileImageUrl,
                    conditions = !user.profileImageUrl.isNullOrEmpty(),
                    isAlpha = false
                ) {}

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = user.username,
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
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomSearchBar(
    hint: String,
    modifier: Modifier = Modifier,
    isEnabled: (Boolean) = true,
    height: Dp = 40.dp,
    elevation: Dp = 3.dp,
    cornerShape: RoundedCornerShape = RoundedCornerShape(8.dp),
    backgroundColor: Color = Color.White,
    onSearchClicked: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    viewModel: ViewModel,
    uiState: MutableStateFlow<List<ChatUser>>,
) {
    var text by remember { mutableStateOf(TextFieldValue()) }
    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape)
            .clickable { onSearchClicked() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            value = text,
            onValueChange = {
                text = it
                onTextChange(it.text)
            },
            enabled = isEnabled,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = with(LocalDensity.current) { (16 / fontScale).sp },
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                if (text.text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = with(LocalDensity.current) { (16 / fontScale).sp },
                        fontWeight = FontWeight.Bold,
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                val resultUsers = mutableListOf<ChatUser>()               // 検索結果に一致したユーザー情報

                if (text.text.isEmpty()) {
                    uiState.value = resultUsers
                } else if (recentMessages.isEmpty()) {
                    // 最新メッセージが空の場合
                    resultUsers.addAll(
                        viewModel.allUsers.filter { user ->
                            // 全ユーザーの中で、既に友達申請しているユーザーを排除する
                            viewModel.friends.none { friend -> friend?.uid == user.uid } &&
                                    // 検索ワードに一致するユーザー名のみ含める
                                    user.username.contains(text.text, ignoreCase = true)
                        }
                    )
                    uiState.value = resultUsers
                } else {
                    // 検索ワードに一致するユーザーをフィルタ
                    val filteredUsers = viewModel.allUsers.filter { user ->
                        user.username.startsWith(text.text)
//                        user.username.contains(text.text, ignoreCase = true)
                    }

                    // 最近のメッセージのトーク相手のUIDを取得
                    val recentMessageIds = viewModel.recentMessages.map { message ->
                        if (viewModel.currentUser.value?.uid == message?.fromId) message?.toId else message?.fromId
                    }

                    // 最近メッセージをやり取りした相手を省く
                    resultUsers.addAll(
                        filteredUsers.filter { user ->
                            !recentMessageIds.contains(user.uid)
                        }
                    )
                    uiState.value = resultUsers
                }
            }),
            singleLine = true
        )
        Box(
            modifier = modifier
                .weight(1f)
                .size(40.dp)
                .background(color = Color.Transparent, shape = CircleShape)
                .clickable {
                    if (text.text.isNotEmpty()) {
                        text = TextFieldValue(text = "")
                        onTextChange("")
                    }
                },
        ) {
            if (text.text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    painter = painterResource(id = R.drawable.baseline_clear_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}