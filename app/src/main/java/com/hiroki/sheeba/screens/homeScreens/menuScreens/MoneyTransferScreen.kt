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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hiroki.sheeba.model.RecentMessage
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun MoneyTransferScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
//                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTopAppBar(
                    title = "",
                    color = Color.White,
                    onButtonClicked = {
                        navController.navigate(Setting.homeScreen)
                    }
                )

                // タブ
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    contentColor = Color.Black,
                    containerColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            color = Color.Black,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("トーク") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("友達") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                when (selectedTabIndex) {
                    0 -> {
                        // トークユーザー
                        LazyColumn {
                            items(viewModel.recentMessages) {
                                if (it != null) {
                                    CustomListRecentMessage(viewModel = viewModel, rm = it) {
//                                        viewModel.fetchMessages(
//                                            toId = if (viewModel.currentUser.value?.uid == it.fromId ) it.toId else it.fromId
//                                        )
                                        viewModel.fetchUser(uid = if (viewModel.currentUser.value?.uid == it.fromId ) it.toId else it.fromId)
//                                        viewModel.chatUserUid = if (viewModel.currentUser.value?.uid == it.fromId ) it.toId else it.fromId
                                        navController.navigate(Setting.chatLogScreen)
                                    }
                                }
                                // 最後の行のみ空白を入れる
                                if(viewModel.recentMessages.last() == it) {
                                    Spacer(modifier = Modifier.height(100.dp))
                                }
                            }
                        }
                    }
                    1 -> {
                        Text("友達")
                    }
                }



                Spacer(modifier = Modifier.height(80.dp))
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
fun CustomListRecentMessage(viewModel: ViewModel, rm: RecentMessage, onButtonClicked: () -> Unit) {
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

            ) {
                CustomImagePicker(
                    size = 60,
                    model = rm.profileImageUrl,
                    conditions = !rm.profileImageUrl.isNullOrEmpty(),
                    isAlpha = false
                ) {}

                Spacer(modifier = Modifier.width(20.dp))

                Column {

                    Text(
                        text = rm.username,
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
                        text = if(rm.isSendPay) {
                            if(viewModel.currentUser.value?.uid == rm.fromId) {
                                "${rm.text}pt送りました"
                            } else {
                                "${rm.text}pt受け取りました"
                            }
                        } else {
                            rm.text
                        },
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
            }
        }
    }
}
