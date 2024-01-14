package com.example.sheeba.screens.signUpScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sheeba.app.PostOfficeAppRouter
import com.example.sheeba.app.Screen
import com.example.sheeba.data.SignUpUIEvent
import com.example.sheeba.screens.components.CustomCapsuleButton
import com.example.sheeba.screens.components.CustomTopAppBar
import com.example.sheeba.screens.components.InputPasswordTextField
import com.example.sheeba.screens.components.InputTextField
import com.example.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun SetUpEmailScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            CustomTopAppBar(
                title = "新規アカウントを作成",
                onButtonClicked = {
                    PostOfficeAppRouter.navigateTo(Screen.SetUpUsernameScreen)
                }
            )

            Spacer(modifier = Modifier.height((screenHeight / 7).dp))

            InputTextField(
                label = "メールアドレス",
                onTextSelected = {
                    viewModel.onEvent(SignUpUIEvent.EmailChange(it))
                },
                errorStatus = viewModel.signUpUIState.value.emailError
            )

            Spacer(modifier = Modifier.height((screenHeight / 25).dp))

            InputPasswordTextField(
                label = "パスワード",
                onTextSelected = {
                    viewModel.onEvent(SignUpUIEvent.PasswordChange(it))
                },
                errorStatus = viewModel.signUpUIState.value.passwordError
            )

            Spacer(modifier = Modifier.height((screenHeight / 25).dp))

            InputPasswordTextField(
                label = "パスワード（確認用）",
                onTextSelected = {
                    viewModel.onEvent(SignUpUIEvent.PasswordChange(it))
                },
                errorStatus = viewModel.signUpUIState.value.passwordError
            )

            Spacer(modifier = Modifier.height((screenHeight / 5).dp))

            CustomCapsuleButton(
                value = "アカウント作成",
                onButtonClicked = {
                    // TODO: -  実行して確認
                    viewModel.onEvent(SignUpUIEvent.RegisterButtonClicked)
                },
                isEnabled = true
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfSetUpEmailScreen() {
    SetUpEmailScreen(viewModel = ViewModel())
}