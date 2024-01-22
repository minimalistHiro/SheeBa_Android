package com.hiroki.sheeba.screens.homeScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.hiroki.sheeba.R
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(viewModel: ViewModel, padding: PaddingValues, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // Screen開示処理
    viewModel.fetchCurrentUser()

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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(Color.LightGray)
//                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(size = 30.dp), clip = false)
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
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
                                Text(
                                    text = viewModel.currentUser.value.money,
                                    style = TextStyle(
                                        fontSize = 35.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Normal,
                                    ),
                                    textAlign = TextAlign.Center,
                                )
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