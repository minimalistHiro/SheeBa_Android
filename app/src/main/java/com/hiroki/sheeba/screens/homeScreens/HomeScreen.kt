package com.hiroki.sheeba.screens.homeScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.NotificationModel
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.MenuButton
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: ViewModel, padding: PaddingValues, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var isContainNotReadNotification = remember {
        mutableStateOf(false)
    }                                   // 外部リンクURL

    // Screen開示処理
    viewModel.init()
    viewModel.fetchCurrentUser()
    viewModel.fetchAlerts()

    // 未読のお知らせを確認する。
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { return }
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.notifications)
        .document(uid)
        .collection(FirebaseConstants.notification)
        .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            for(document in documents) {
                document.toObject(NotificationModel::class.java)?.let {
                    // 未読があった場合、お知らせに赤いバッジをつける
                    if(!it.isRead) {
                        isContainNotReadNotification.value = true
                    }
                }
            }
        }
        .addOnFailureListener { exception ->
            viewModel.handleError(title = "", text = Setting.failureFetchNotification, exception = exception)
        }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // トップバー
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cleartitle),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .size(width = 150.dp, height = 60.dp),
                    )

                    // お知らせ
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp)
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(30.dp),
                            onClick = {
                                viewModel.fetchNotifications()
                                navController.navigate(Setting.notificationListScreen)
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_notifications_24),
                                contentDescription = "",
                                tint = Color.Black,
                            )
                        }

                        // バッジ
                        if(isContainNotReadNotification.value) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red),
                            )
                        }
                    }
                }

                // アラートバー
                if(viewModel.alertNotification != null) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .clip(androidx.compose.ui.graphics.RectangleShape)
                            .background(Color.Blue)
                            .fillMaxWidth()
                            .height(45.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = viewModel.alertNotification?.title ?: "",
                                fontSize = with(LocalDensity.current) { (12 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )

                            Text(
                                text = viewModel.alertNotification?.text ?: "",
                                fontSize = with(LocalDensity.current) { (12 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                        }
                    }
                }

                // カードスクリーン
                Box(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(size = 30.dp))
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(colorResource(id = R.color.sheebaYellow))
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "獲得ポイント",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width((screenWidth / 5).dp))

                            if(viewModel.progress.value) {
                                CircularProgressIndicator()
                            } else {
                                viewModel.currentUser.value?.let {
                                    Text(
                                        text = it.money,
                                        style = TextStyle(
                                            fontSize = 35.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Normal,
                                        ),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "pt",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.width((screenWidth / 15).dp))
                            IconButton(
                                onClick = {
                                    viewModel.fetchCurrentUser()
                                },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_replay_24),
                                    contentDescription = ""
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                Spacer(modifier = Modifier.height((screenHeight / 30).dp))

                // Menuボタン
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(colorResource(id = R.color.sheebaDarkGreen))
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row {
                        // ランキング
                        MenuButton(text = "ランキング", painter = painterResource(id = R.drawable.baseline_flag_24)) {
                            viewModel.fetchAllUsersOrderByMoney()
                            navController.navigate(Setting.rankingScreen)
                        }
                        // 本日の獲得
                        MenuButton(text = "本日の獲得", painter = painterResource(id = R.drawable.baseline_store_24)) {
                            viewModel.fetchStorePoints()
                            navController.navigate(Setting.todaysGetPointScreen)
                        }
                    }
                }
            }
        }
        // ダイアログ
        if(viewModel.isShowDialog.value) {
            CustomAlertDialog(
                title = viewModel.dialogTitle.value,
                text = viewModel.dialogText.value) {
                viewModel.isShowDialog.value = false
            }
        }
        // ログアウトへと誘導するダイアログ
        if(viewModel.isShowDialogForLogout.value) {
            CustomAlertDialog(
                title = viewModel.dialogTitle.value,
                text = viewModel.dialogText.value) {
                viewModel.isShowDialogForLogout.value = false
                viewModel.isShowCompulsionLogoutDialog.value = true
            }
        }
        // 強制ログアウトダイアログ
        if(viewModel.isShowCompulsionLogoutDialog.value) {
            CustomAlertDialog(
                title = "",
                text = "エラーが発生したためログアウトします。") {
                viewModel.handleLogout()
                viewModel.isShowCompulsionLogoutDialog.value = false
            }
        }
    }
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfHomeScreen() {
    HomeScreen(
        viewModel = ViewModel(),
        padding = PaddingValues(all = 20.dp),
        navController = rememberNavController(),
    )
}