package com.hiroki.sheeba.screens.signUpScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputEmailTextField
import com.hiroki.sheeba.screens.components.InputPasswordTextField
import com.hiroki.sheeba.viewModel.ViewModel


@ExperimentalMaterial3Api
@Composable
fun SetUpEmailScreen(viewModel: ViewModel) {
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
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                CustomTopAppBar(
                    title = "新規アカウントを作成",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.SetUpUsernameScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 7).dp))

                InputEmailTextField(
                    label = "メールアドレス",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.EmailChange(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.emailError
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                InputPasswordTextField(
                    label = "パスワード",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.PasswordChange(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.passwordError
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                InputPasswordTextField(
                    label = "パスワード（確認用）",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.Password2Change(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.password2Error
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                CustomCapsuleButton(
                    text = "アカウント作成",
                    onButtonClicked = {
                        viewModel.onSignUpEvent(SignUpUIEvent.SignUpButtonClicked)
                    },
                    isEnabled = viewModel.signUpAllValidationPassed.value
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
fun DefaultPreviewOfSetUpEmailScreen() {
    SetUpEmailScreen(viewModel = ViewModel())
}