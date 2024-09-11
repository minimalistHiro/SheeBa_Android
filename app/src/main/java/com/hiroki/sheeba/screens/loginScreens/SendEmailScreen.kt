package com.hiroki.sheeba.screens.loginScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputEmailTextField
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun SendEmailScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var isShowInvalidLinkCautionDialog = remember {
        mutableStateOf(false)
    }                                   // メールリンク無効警告表示有無
    var isSendEmailDialog = remember {
        mutableStateOf(false)
    }                                   // メール送信ダイアログ

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CustomTopAppBar(
                    title = "新規アカウントを作成",
                    color = colorResource(id = R.color.sheebaYellow),
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 7).dp))

                Text(
                    text = "入力したメールアドレスに\nパスワード再設定用のURLを送信します。",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                InputEmailTextField(
                    label = "メールアドレス",
                    onTextSelected = {
                        viewModel.onLoginEvent(LoginUIEvent.EmailChange(it))
                    },
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                CustomCapsuleButton(
                    text = "メール送信",
                    onButtonClicked = {
                        viewModel.handleSendResetPasswordLink()
                    },
                    isEnabled = (!viewModel.loginUIState.value.email.isEmpty()),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))
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
                PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfSendEmailScreen() {
    SendEmailScreen(viewModel = ViewModel())
}