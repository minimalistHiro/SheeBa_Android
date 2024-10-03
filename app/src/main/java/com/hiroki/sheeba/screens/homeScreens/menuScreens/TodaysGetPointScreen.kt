package com.hiroki.sheeba.screens.homeScreens.menuScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hiroki.sheeba.R
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomStoreCard
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.mapScreens.NavStoreDetailScreen
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun TodaysGetPointScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

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
                CustomTopAppBar(
                    title = "本日の獲得ポイント一覧",
                    color = Color.White,
                    onButtonClicked = {
                        navController.navigate(Setting.homeScreen)
                    }
                )

                // 獲得ポイント
                Box(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .clip(RoundedCornerShape(size = 30.dp))
                        .background(colorResource(id = R.color.sheebaDarkGreen))
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = "本日",
                            fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${viewModel.countGetStorePointToday()} / ${viewModel.stores.size}",
                                fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Text(
                                text = "店舗獲得",
                                fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                // 店舗一覧
                viewModel.stores.forEach { store ->
                    if (store != null) {
                        CustomStoreCard(
                            store = store,
                            isGetPoint = viewModel.isGetStorePointToday(store = store),
                            onButtonClicked = {
                                viewModel.store.value = store
                                viewModel.navStoreDetailScreen = NavStoreDetailScreen.TodaysGetPointScreen
                                navController.navigate(Setting.storeDetailScreen)
                            }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                Spacer(modifier = Modifier.height((screenHeight / 7).dp))
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
fun DefaultPreviewOfTodaysGetPointScreen() {
    TodaysGetPointScreen(viewModel = ViewModel(), navController = rememberNavController())
}