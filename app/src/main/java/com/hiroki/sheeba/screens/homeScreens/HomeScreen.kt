package com.hiroki.sheeba.screens.homeScreens

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import com.hiroki.sheeba.model.ChatMessage
import com.hiroki.sheeba.model.NotificationModel
import com.hiroki.sheeba.model.RecentMessage
import com.hiroki.sheeba.model.StorePoint
import com.hiroki.sheeba.model.Stores
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomRankingCard
import com.hiroki.sheeba.screens.components.MenuButton
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.storePoints
import com.hiroki.sheeba.util.FirebaseConstants.uid
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: ViewModel, padding: PaddingValues, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    var isContainNotReadNotification = remember {
        mutableStateOf(false)
    }                                   // 外部リンクURL

    // 獲得店舗
    val _uiSPState = MutableStateFlow(listOf<StorePoint>())
    val uiSPState: StateFlow<List<StorePoint>> = _uiSPState.asStateFlow()
    val storePoints by uiSPState.collectAsState()

    // イベント店舗
    val _uiESState = MutableStateFlow(listOf<Stores>())
    val uiESState: StateFlow<List<Stores>> = _uiESState.asStateFlow()
    val eventStores by uiESState.collectAsState()

    // Screen開示処理
    viewModel.init()
    viewModel.fetchCurrentUser()
    viewModel.fetchAlerts()
//    viewModel.fetchStorePointsForEventStore()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        return
    }

    // 店舗ポイント情報を取得
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.storePoints)
        .document(uid)
        .collection(FirebaseConstants.user)
        .addSnapshotListener { documents, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val storePoints = mutableListOf<StorePoint>()
            for(document in documents!!) {
                document.toObject(StorePoint::class.java)?.let {
                    storePoints.add(it)
                }
            }
            _uiSPState.value = storePoints
        }

    // 全イベント店舗を取得
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.stores)
        .orderBy(FirebaseConstants.no)
        .addSnapshotListener { documents, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val eventStores = mutableListOf<Stores>()
            for (document in documents!!) {
                document.toObject(Stores::class.java).let {
                    if (it.isEnableScan && it.isEvent) {
                        eventStores.add(it)
                    }
                }
            }
            _uiESState.value = eventStores
        }

    // 未読のお知らせを確認する。
    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.notifications)
        .document(uid)
        .collection(FirebaseConstants.notification)
        .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
        .addSnapshotListener { documents, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (document in documents!!) {
                document.toObject(NotificationModel::class.java).let {
                    // 未読があった場合、お知らせに赤いバッジをつける
                    if(!it.isRead) {
                        isContainNotReadNotification.value = true
                    }
                }
            }
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
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
//                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
//                    Spacer(modifier = Modifier.padding(start = 10.dp).size(25.dp))

//                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(id = R.drawable.cleartitle),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .width(120.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // お知らせ
                    Box(
                        modifier = Modifier
//                            .fillMaxSize()
                            .padding(top = 10.dp)
                            .padding(end = 10.dp),
//                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            modifier = Modifier
                                .size(30.dp),
                            onClick = {
                                viewModel.fetchNotifications()
                                // 店舗オーナーのみ、全ユーザーを取得
                                viewModel.currentUser.value?.let {
                                    if (it.isStoreOwner) {
                                        viewModel.fetchAllUsersContainSelf()
                                    }
                                }
                                navController.navigate(Setting.notificationListScreen)
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_notifications_24),
                                contentDescription = "",
                                tint = Color.Black,
                                modifier = Modifier.size(25.dp),
                            )
                        }

                        // バッジ
                        if(isContainNotReadNotification.value) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                                    .padding(end = 10.dp, top = 10.dp),
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

                Image(
                    painter = painterResource(id = R.drawable.sheeba1),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                )

                // カードスクリーン
                Box(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(size = 30.dp))
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(colorResource(id = R.color.sheebaYellow))
                        .width(270.dp)
                        .height(180.dp),
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

                Spacer(modifier = Modifier.height(25.dp))

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
                        // QRコード
                        MenuButton(
                            text = "QRコード",
                            painter = painterResource(id = R.drawable.baseline_qr_code_24)
                        ) {
                            navController.navigate(Setting.qrCodeScreen)
                        }
                        // 送る
                        MenuButton(
                            text = "送る",
                            painter = painterResource(id = R.drawable.baseline_currency_yen_24)
                        ) {
                            viewModel.fetchRecentMessages()
                            viewModel.fetchFriends()
                            navController.navigate(Setting.moneyTransferScreen)
                        }
                        // ランキング
                        MenuButton(
                            text = "ランキング",
                            painter = painterResource(id = R.drawable.baseline_flag_24)
                        ) {
                            viewModel.fetchAllUsersOrderByMoney()
                            navController.navigate(Setting.rankingScreen)
                        }
                        // 本日の獲得
                        MenuButton(
                            text = "本日の獲得",
                            painter = painterResource(id = R.drawable.baseline_store_24)
                        ) {
                            viewModel.fetchStorePoints()
                            navController.navigate(Setting.todaysGetPointScreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                // イベントバッジ
                Box(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(size = 30.dp))
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(Color.White)
                        .width(300.dp)
                        .height(400.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = Modifier.background(colorResource(id = R.color.sheebaOrange)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "ハロウィンスタンプ",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            text = "獲得可能期間10月25日〜10月31日",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )

//                        Spacer(modifier = Modifier.height(20.dp))

                        LazyVerticalGrid(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(30.dp),
                            columns = GridCells.Fixed(count = 4),
                            contentPadding = PaddingValues(top = 20.dp, start = 15.dp, end = 15.dp),
//                            modifier = Modifier.fillMaxWidth()
                        ) {
                            eventStores.forEach { store ->
                                item {
                                    CustomImagePicker(
                                        size = 45,
                                        model = store.profileImageUrl,
                                        isAlpha = !isGetEventStorePoint(store, storePoints),
                                        conditions = (!store.profileImageUrl.isEmpty())) {}
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))          // 空白のスペースを追加
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
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

/**
 * イベント店舗ポイント取得済みか否かを判断
 *
 * @param store 店舗
 * @return 全店舗ユーザーの中にイベント店舗ポイント情報を確保していた場合True、そうでない場合false。
 */
fun isGetEventStorePoint(store: Stores, storePoints: List<StorePoint?>): Boolean {
    for (storePoint in storePoints) {
        // 全店舗ユーザーの中にイベント店舗ポイント情報を確保していた場合True。
        if (storePoint != null) {
            if (store.uid == storePoint.uid) {
                return true
            }
        }
    }
    return false
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