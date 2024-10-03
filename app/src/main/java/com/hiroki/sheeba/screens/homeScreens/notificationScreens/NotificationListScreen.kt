package com.hiroki.sheeba.screens.homeScreens.notificationScreens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import com.hiroki.sheeba.model.Friends
import com.hiroki.sheeba.model.NotificationModel
import com.hiroki.sheeba.model.RecentMessage
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomListNav
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.notifications
import com.hiroki.sheeba.util.FirebaseConstants.recentMessages
import com.hiroki.sheeba.util.FirebaseConstants.uid
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalMaterial3Api
@Composable
fun NotificationListScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // お知らせ
    val _uiNOState = MutableStateFlow(listOf<NotificationModel>())
    val uiNOState: StateFlow<List<NotificationModel>> = _uiNOState.asStateFlow()
    val notifications by uiNOState.collectAsState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
        return
    }

    // 全お知らせを取得
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
            val notifications = mutableListOf<NotificationModel>()
            for (document in documents!!) {
                document.toObject(NotificationModel::class.java).let {
                    notifications.add(it)
                }
            }
            _uiNOState.value = notifications
        }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
//                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTopAppBar(
                    title = "お知らせ",
                    color = Color.White,
                    onButtonClicked = {
                        navController.navigate(Setting.homeScreen)
                    }
                )

//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                ) {
                    // 全お知らせ
                    LazyColumn {
                        item {
                            notifications.map {
                                if (it != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
//                                    CustomListNav(text = it.title, color = Color.Black) {
//                                        viewModel.notification = it
//                                        navController.navigate(Setting.notificationDetailScreen)
//                                    }
                                        CustomListNotification(notification = it) {
                                            viewModel.notification = it
                                            navController.navigate(Setting.notificationDetailScreen)
                                        }
                                    }
                                    CustomDivider(color = Color.Gray)

                                    // 最後の行のみ空白を入れる
                                    if(notifications.last() == it) {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }
                            }
                        }
                    }

//                    viewModel.notifications.forEach { notification ->
//                        if (notification != null) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize(),
//                                verticalAlignment = Alignment.CenterVertically,
//                            ) {
//                                CustomListNav(text = notification.title, color = Color.Black) {
//                                    viewModel.notification = notification
//                                    navController.navigate(Setting.notificationDetailScreen)
//                                }
//
//                                // バッジ
//                                if(!notification.isRead) {
//                                    Box(
//                                        modifier = Modifier
//                                            .size(15.dp)
//                                            .clip(CircleShape)
//                                            .background(Color.Red),
//                                    )
//                                }
//                            }
//                            CustomDivider(color = Color.Gray)
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
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomListNotification(notification: NotificationModel , onButtonClicked: () -> Unit) {
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
                    model = notification.profileImageUrl,
                    conditions = !notification.profileImageUrl.isNullOrEmpty(),
                    isAlpha = false
                ) {}

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = notification.title,
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
                        text = notification.username,
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
                if(!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfNotificationListScreen() {
    NotificationListScreen(
        viewModel = ViewModel(),
        navController = rememberNavController(),
    )
}