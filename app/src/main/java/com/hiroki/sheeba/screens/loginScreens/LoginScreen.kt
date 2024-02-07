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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomTextButton
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputEmailTextField
import com.hiroki.sheeba.screens.components.InputPasswordTextField
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

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
                    title = "ログイン",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 7).dp))

                InputEmailTextField(
                    label = "メールアドレス",
                    onTextSelected = {
                        viewModel.onLoginEvent(LoginUIEvent.EmailChange(it))
                    },
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                InputPasswordTextField(
                    label = "パスワード",
                    onTextSelected = {
                        viewModel.onLoginEvent(LoginUIEvent.PasswordChange(it))
                    },
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                CustomCapsuleButton(
                    text = "ログイン",
                    onButtonClicked = {
                        viewModel.onLoginEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = (!viewModel.loginUIState.value.email.isEmpty()) &&
                            (!viewModel.loginUIState.value.password.isEmpty())
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                CustomTextButton(
                    text = "パスワードを忘れた方はこちら",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.SendEmailScreen)
                    },
                    color = Color.Blue
                )
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
fun DefaultPreviewOfLoginScreen() {
    LoginScreen(viewModel = ViewModel())
}