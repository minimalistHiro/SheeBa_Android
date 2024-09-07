package com.hiroki.sheeba.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hiroki.sheeba.R
import com.hiroki.sheeba.data.BottomNavigationItem
import com.hiroki.sheeba.screens.accountScreens.AccountScreen
import com.hiroki.sheeba.screens.accountScreens.UpdateImageScreen
import com.hiroki.sheeba.screens.accountScreens.UpdateUsernameScreen
import com.hiroki.sheeba.screens.cameraScreens.CameraScreen
import com.hiroki.sheeba.screens.cameraScreens.ErrorQRCodeOverlay
import com.hiroki.sheeba.screens.cameraScreens.QrCodeOverlay
import com.hiroki.sheeba.screens.homeScreens.HomeScreen
import com.hiroki.sheeba.screens.homeScreens.menuScreens.ChatLogScreen
import com.hiroki.sheeba.screens.homeScreens.menuScreens.MoneyTransferScreen
import com.hiroki.sheeba.screens.homeScreens.menuScreens.QRCodeScreen
import com.hiroki.sheeba.screens.homeScreens.menuScreens.RankingScreen
import com.hiroki.sheeba.screens.homeScreens.menuScreens.TodaysGetPointScreen
import com.hiroki.sheeba.screens.homeScreens.notificationScreens.NotificationDetailScreen
import com.hiroki.sheeba.screens.homeScreens.notificationScreens.NotificationListScreen
import com.hiroki.sheeba.screens.mapScreens.MapScreen
import com.hiroki.sheeba.screens.mapScreens.StoreDetailScreen
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun ContentScreen(viewModel: ViewModel, navController: NavHostController) {
//    var selectedTabIndex by remember { mutableStateOf(0) }          // 選択されたタブ番号
    // ボトムタブ
    val items = listOf(
        BottomNavigationItem(
            title = "ホーム",
            navTitle = Setting.homeScreen,
            selectedIcon = painterResource(id = R.drawable.baseline_home_24),
            unselectedIcon = painterResource(id = R.drawable.baseline_home_24),
        ),
        BottomNavigationItem(
            title = "マップ",
            navTitle = Setting.mapScreen,
            selectedIcon = painterResource(id = R.drawable.baseline_location_pin_24),
            unselectedIcon = painterResource(id = R.drawable.baseline_location_pin_24),
        ),
        BottomNavigationItem(
            title = "スキャン",
            navTitle = Setting.cameraScreen,
            selectedIcon = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
            unselectedIcon = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
        ),
        BottomNavigationItem(
            title = "マップ",
            navTitle = Setting.mapScreen,
            selectedIcon = painterResource(id = R.drawable.baseline_location_pin_24),
            unselectedIcon = painterResource(id = R.drawable.baseline_location_pin_24),
        ),
        BottomNavigationItem(
            title = "アカウント",
            navTitle = Setting.accountScreen,
            selectedIcon = painterResource(id = R.drawable.baseline_person_24),
            unselectedIcon = painterResource(id = R.drawable.baseline_person_24),
        ),
    )
    // 画面遷移の状態を扱う変数を追加
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    // Screen開示処理
    viewModel.fetchCurrentUser()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                // スキャン可能状態に戻す
                                viewModel.isShowHandleScan.value = false
                                // Screen開示処理
                                viewModel.chatUser.value = null
                                viewModel.storePoint.value = null

                                // 画面遷移
                                selectedItemIndex = index
                                navController.navigate(item.navTitle)
                            },
                            label = { Text(
                                text = item.title,

                                color = colorResource(id = R.color.sheebaDarkGreen),
                            ) },
                            icon = {
                                Icon(
                                    painter = if(index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = colorResource(id = R.color.sheebaDarkGreen)
                                )
                            },
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = items[selectedItemIndex].navTitle
            ) {
                composable(items[0].navTitle) {
                    HomeScreen(viewModel = viewModel, padding = padding, navController = navController)
                }
                composable(items[1].navTitle) {
                    MapScreen(viewModel = viewModel, navController = navController)
                }
                composable(items[2].navTitle) {
                    CameraScreen(viewModel = viewModel, padding = padding, navController = navController, viewModel.qrCodeAnalyzeUseCase)
                    viewModel.qrCode.value?.let {
                        QrCodeOverlay(qrCode = it)
                    }
                    if(viewModel.isQrCodeScanError.value) {
                        ErrorQRCodeOverlay()
                    }
                }
                composable(items[3].navTitle) {
                    MapScreen(viewModel = viewModel, navController = navController)
                }
                composable(items[4].navTitle) {
                    AccountScreen(viewModel = viewModel, padding = padding, navController = navController)
                }
                composable(Setting.notificationListScreen) {
                    NotificationListScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.notificationDetailScreen) {
                    NotificationDetailScreen(viewModel = viewModel, navController = navController, notification = viewModel.notification)
                }
                composable(Setting.moneyTransferScreen) {
                    MoneyTransferScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.chatLogScreen) {
                    ChatLogScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.storeDetailScreen) {
                    StoreDetailScreen(viewModel = viewModel, navController = navController, storeUser = viewModel.storeUser, nav = viewModel.navStoreDetailScreen)
                }
                composable(Setting.qrCodeScreen) {
                    QRCodeScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.rankingScreen) {
                    RankingScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.todaysGetPointScreen) {
                    TodaysGetPointScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.updateImageScreen) {
                    UpdateImageScreen(viewModel = viewModel, navController = navController)
                }
                composable(Setting.updateUsernameScreen) {
                    UpdateUsernameScreen(viewModel = viewModel, navController = navController)
                }
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfContentScreen() {
    ContentScreen(viewModel = ViewModel(), navController = rememberNavController())
}