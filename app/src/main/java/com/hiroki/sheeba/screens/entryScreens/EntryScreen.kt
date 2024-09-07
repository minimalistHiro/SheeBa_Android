package com.hiroki.sheeba.screens.entryScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomBorderCapsuleButton
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDoubleAlertDialog
import com.hiroki.sheeba.screens.components.CustomDoubleTextAlertDialog
import com.hiroki.sheeba.util.FirebaseConstants.text
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun EntryScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var isShowConfirmStoreOwner = remember {
        mutableStateOf(false)
    }

    // 初期化処理
    viewModel.init()

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
            ) {
                Spacer(modifier = Modifier.height((screenHeight / 3).dp))

                Image(
                    painter = painterResource(id = R.drawable.title),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 60.dp)
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                CustomBorderCapsuleButton(
                    text = "アカウントを作成する",
                    onButtonClicked = {
                        isShowConfirmStoreOwner.value = true
                    },
                    isEnabled = true
                )

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                CustomCapsuleButton(
                    text = "ログイン",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
                    },
                    isEnabled = true
                )
            }
        }
        // 店舗オーナー確認ダイアログ
        if(isShowConfirmStoreOwner.value) {
            CustomDoubleTextAlertDialog(
                title = "",
                text = "一般ユーザーとしてアカウントを作成しますか？",
                okText = "一般ユーザー",
                cancelText = "店舗オーナー",
                onOkButtonClicked = {
                    viewModel.isStoreOwner.value = false
                    PostOfficeAppRouter.navigateTo(Screen.SetUpUsernameScreen)
                },
                onCancelButtonClicked =  {
                    viewModel.isStoreOwner.value = true
                    PostOfficeAppRouter.navigateTo(Screen.SetUpUsernameScreen)
                }
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfEntryScreen() {
    EntryScreen(viewModel = ViewModel())
}