package com.hiroki.sheeba.screens.homeScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomDoubleAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun SendPayScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val keyboards = listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "00", "AC")
    val isShowSendPayDialog = remember {
        mutableStateOf(false)
    }                                                       // 送ポイントダイアログ

    // 初期化処理
    viewModel.init()

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTopAppBar(
                    title = "",
                    color = colorResource(id = R.color.sheebaYellow),
                    onButtonClicked = {
                        viewModel.sendPayText.value = "0"
                        PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // アイコン画像
                CustomImagePicker(
                    size = 120,
                    model = viewModel.chatUser.value?.profileImageUrl,
                    isAlpha = false,
                    conditions = (viewModel.chatUser.value?.profileImageUrl != "" && viewModel.chatUser.value != null)) {}

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "さんに送る",
                        fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.sendPayText.value,
                        fontSize = with(LocalDensity.current) { (60 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
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

                Button(
                    modifier = Modifier
                        .widthIn(100.dp)
                        .heightIn(60.dp),
                    onClick = {
                        isShowSendPayDialog.value = true
                    },
                    enabled = viewModel.sendPayText.value.toInt() > 0,
                    colors = ButtonDefaults.buttonColors(
                        if(viewModel.sendPayText.value.toInt() > 0) {
                            Color.Blue
                        } else {
                            Color.Gray
                        }
                    )
                ) {
                    Text(text =  "送る",
                        fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy((screenHeight / 25).dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    items(
                        items = keyboards,
                        span = { GridItemSpan(1) },
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.applyKeyboard(it)
                            },
                        ) {
                            Text(
                                text = it,
                                fontSize = with(LocalDensity.current) { (35 / fontScale).sp },
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                color = if(it == "AC") {
                                    Color.Red
                                } else {
                                    Color.Black
                                }
                            )
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
        // 送ポイントダイアログ
        if(isShowSendPayDialog.value) {
            CustomDoubleAlertDialog(
                title = "",
                text = "${viewModel.sendPayText.value}pt送りますか？",
                okText = "送る",
                onOkButtonClicked = {
                    viewModel.handleSendPoint(navController = navController, isQRScan = false)
                    viewModel.chatUser.value?.let {
                        viewModel.handleSend(toId = it.uid, chatText = "", lastText = viewModel.sendPayText.value, isSendPay = true)
                    }
                    viewModel.sendPayText.value = "0"
                    isShowSendPayDialog.value = false
                },
                onCancelButtonClicked = {
                    isShowSendPayDialog.value = false
                },
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfSendPayScreen() {
    SendPayScreen(
        viewModel = ViewModel(),
        navController = rememberNavController(),
    )
}