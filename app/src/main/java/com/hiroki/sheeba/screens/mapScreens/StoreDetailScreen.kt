package com.hiroki.sheeba.screens.mapScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.screens.accountScreens.ExternalLink
import com.hiroki.sheeba.screens.components.CustomAsyncImage
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomListNav
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.FirebaseConstants.text
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

enum class NavStoreDetailScreen {
    TodaysGetPointScreen,
    MapScreen,
}

@ExperimentalMaterial3Api
@Composable
fun StoreDetailScreen(viewModel: ViewModel, navController: NavController, storeUser: ChatUser?, nav: NavStoreDetailScreen) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val uriHandler = LocalUriHandler.current                                // URL開示用変数

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
                    title = "",
                    color = Color.White,
                    onButtonClicked = {
                        when (nav) {
                            NavStoreDetailScreen.TodaysGetPointScreen -> navController.navigate(Setting.todaysGetPointScreen)
                            NavStoreDetailScreen.MapScreen -> navController.navigate(Setting.mapScreen)
                        }
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                // トップ画像
                CustomImagePicker(
                    size = 120,
                    model = viewModel.storeUser?.profileImageUrl,
                    isAlpha = false,
                    conditions = (!viewModel.storeUser?.profileImageUrl.isNullOrEmpty())) {}

                Spacer(modifier = Modifier.height(20.dp))

                // 店舗名
                viewModel.storeUser?.username?.let {
                    Text(
                        text = it,
                        fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    viewModel.storeUser?.let {
                        // ジャンル
                        CustomStoreDetailList(title = "ジャンル", text = it.genre, color = Color.Black) {}
                        CustomDivider(color = Color.Gray)

                        // 電話番号
                        // 公式サイト
                        CustomStoreDetailList(title = "電話番号", text = it.phoneNumber, color = Color.Blue) {
                            // TODO: - 電話をかける
                        }
                        CustomDivider(color = Color.Gray)

                        // Webサイト
                        CustomStoreDetailList(title = "Webサイト", text = it.webURL, color = Color.Blue) {
                            uriHandler.openUri(it.webURL)
                        }
                        CustomDivider(color = Color.Gray)

                        // 紹介動画
                        CustomStoreDetailList(title = "紹介動画", text = it.movieURL, color = Color.Blue) {
                            uriHandler.openUri(it.movieURL)
                        }
                        CustomDivider(color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomStoreDetailList(title: String, text: String, color: Color, onButtonClicked: () -> Unit) {
    TextButton(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .padding(horizontal = 25.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        colors = ButtonDefaults.textButtonColors(Color.White)
    ) {
        Row {
            Text(
                text = title,
                fontSize = with(LocalDensity.current) { (18 / fontScale).sp },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = text,
                fontSize = with(LocalDensity.current) { (18 / fontScale).sp },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
                color = color,
            )
        }
    }
}