package com.hiroki.sheeba.screens.homeScreens.notificationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomListNav
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun NotificationListScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

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
                        items(viewModel.notifications) {
                            if (it != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CustomListNav(text = it.title, color = Color.Black) {
                                        viewModel.notification = it
                                        navController.navigate(Setting.notificationDetailScreen)
                                    }

                                    // バッジ
                                    if(!it.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(15.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red),
                                        )
                                    }
                                }
                                CustomDivider(color = Color.Gray)

                                // 最後の行のみ空白を入れる
                                if(viewModel.notifications.last() == it) {
                                    Spacer(modifier = Modifier.height(100.dp))
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

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfNotificationListScreen() {
    NotificationListScreen(
        viewModel = ViewModel(),
        navController = rememberNavController(),
    )
}