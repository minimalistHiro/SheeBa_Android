package com.hiroki.sheeba.screens.cameraScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun GetPointScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height((screenHeight / 15).dp))

                // アイコン画像
                if(!viewModel.isQrCodeScanError.value) {
                    CustomImagePicker(
                        size = 120,
                        model = viewModel.chatUser.value?.profileImageUrl,
                        conditions = (viewModel.chatUser.value?.profileImageUrl != "" && viewModel.chatUser.value != null)) {}
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = viewModel.chatUser.value?.username?: "エラー",
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                if(viewModel.isSameStoreScanError.value) {
                    Text(
                        text = "1店舗につき1日1回のみポイントが貰えます。",
                        fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                } else if(viewModel.isQrCodeScanError.value) {
                    Text(
                        text = "誤ったQRコードがスキャンされました。",
                        fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = viewModel.getPoint.value,
                            fontSize = with(LocalDensity.current) { (70 / fontScale).sp },
                            style = TextStyle(
                                fontSize = 70.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "pt",
                            fontSize = with(LocalDensity.current) { (30 / fontScale).sp },
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "ゲット！",
                        fontSize = with(LocalDensity.current) { (35 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                CustomCapsuleButton(
                    text = "戻る",
                    onButtonClicked = {
                        // スキャン可能状態に戻す
                        viewModel.isShowHandleScan.value =false
                        // エラーを解除する
                        viewModel.isSameStoreScanError.value = false
                        viewModel.isQrCodeScanError.value = false
                        // 取得情報を初期化
                        viewModel.chatUser.value = null
                        viewModel.storePoint.value = null

                        PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                    },
                    isEnabled = true
                )
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfGetPointScreen() {
    GetPointScreen(
        viewModel = ViewModel(),
    )
}