package com.hiroki.sheeba.screens.homeScreens.notificationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.hiroki.sheeba.model.NotificationModel
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDestructiveAlertDialog
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.title
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import java.util.Date

@ExperimentalMaterial3Api
@Composable
fun NotificationDetailScreen(viewModel: ViewModel, navController: NavHostController, notification: NotificationModel?) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val uriHandler = LocalUriHandler.current            // URL開示用変数
    var isShowDeleteNotificationAlert = remember {
        mutableStateOf(false)
    }                                                   // 削除確認ダイアログ表示有無
    var isShowDeleteSuccessAlert = remember {
        mutableStateOf(false)
    }                                                   // 削除成功ダイアログ表示有無

    // 既読にする
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { return }
    val data = hashMapOf<String, Any>(FirebaseConstants.isRead to true,)
    if (notification != null) {
        viewModel.updateNotification(document1 = uid, document2 = notification.title, data = data)
    }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                CustomTopAppBar(
                    title = notification?.username ?: "",
                    color = Color.White,
                    onButtonClicked = {
                        viewModel.fetchNotifications()
                        navController.navigate(Setting.notificationListScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 30).dp))

                // タイトル
                Text(
                    text = notification?.title ?: "",
                    fontSize = with(LocalDensity.current) { (24 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 30).dp))

                // タイムスタンプ
                Text(
                    text = viewModel.convertDateToString(notification?.timestamp?.toDate() ?: Date()),
                    fontSize = with(LocalDensity.current) { (12 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height((screenHeight / 30).dp))

                // テキスト
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp),
                    text = notification?.text ?: "",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Start,
                )

                // URL
                TextButton(
                    onClick = {
                        if (notification != null) {
                            uriHandler.openUri(notification.url)
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp),
                        text = notification?.url ?: "",
                        fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Start,
                        color = Color.Blue,
                    )
                }

                // 画像
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    model = notification?.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.height(40.dp))

                viewModel.currentUser.value?.let {
                    // オーナー、もしくは自身が作成した店舗オーナー
                    if (it.isOwner || (it.isStoreOwner && notification?.uid == viewModel.currentUser.value?.uid)) {
                        CustomCapsuleButton(text = "削除",
                            onButtonClicked = {
                                isShowDeleteNotificationAlert.value = true
                            },
                            isEnabled = true,
                            color = Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(140.dp))
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
        // お知らせ削除確認ダイアログ
        if(isShowDeleteNotificationAlert.value) {
            CustomDestructiveAlertDialog(
                title = "",
                text = "このお知らせを削除しますか？",
                okText = "削除",
                onOkButtonClicked = {
                    // お知らせを削除
                    notification?.let {
                        for (user in viewModel.allUsersContainSelf) {
                            viewModel.deleteNotification(document1 = user.uid, document2 = it.title)
                        }
                        viewModel.deleteImage(path = it.title)
                    }
                    isShowDeleteNotificationAlert.value = false
                    isShowDeleteSuccessAlert.value = true
                },
                onCancelButtonClicked = {
                    isShowDeleteNotificationAlert.value = false
                },
            )
        }
        // 削除成功ダイアログ
        if(isShowDeleteSuccessAlert.value) {
            CustomAlertDialog(
                title = "",
                text = "削除しました。") {
                navController.navigate(Setting.notificationListScreen)
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfNotificationDetailScreen() {
    NotificationDetailScreen(
        viewModel = ViewModel(),
        navController = rememberNavController(),
        notification = null,
    )
}