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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.hiroki.sheeba.R
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDestructiveAlertDialog
import com.hiroki.sheeba.screens.components.CustomTextButton
import com.hiroki.sheeba.screens.components.InputEmailTextField
import com.hiroki.sheeba.screens.components.InputPasswordTextField
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun NotConfirmEmailScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    var isShowConfirmationLogoutDialog = remember {
        mutableStateOf(false)
    }                                   // ログアウト確認ダイアログの表示有無
    var isShowConfirmationWithdrawalDialog = remember {
        mutableStateOf(false)
    }                                   // データ削除確認ダイアログの表示有無

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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height((screenHeight / 7).dp))

                Text(
                    text = "メールアドレス、パスワードを入力し、\n「メール送信」ボタンを押して\nメールアドレス認証を完了してください。",
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 7).dp))

                if(!viewModel.isHandleLoginProcess.value) {
                    InputEmailTextField(
                        label = "メールアドレス",
                        onTextSelected = {
                            viewModel.onLoginEvent(LoginUIEvent.EmailChange(it))
                        },
                        errorStatus = viewModel.loginUIState.value.emailError
                    )

                    Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                    InputPasswordTextField(
                        label = "パスワード",
                        onTextSelected = {
                            viewModel.onLoginEvent(LoginUIEvent.PasswordChange(it))
                        },
                        errorStatus = viewModel.loginUIState.value.passwordError
                    )
                }

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                CustomCapsuleButton(
                    text = "メール送信",
                    onButtonClicked = {
//                        viewModel.handleLoginWithEmailVerification()
                        viewModel.handleEmailVerification()
                    },
//                    isEnabled = viewModel.loginAllValidationPassed.value
                    isEnabled = true
                )

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                CustomTextButton(
                    text = "ログアウト",
                    onButtonClicked = {
                        isShowConfirmationLogoutDialog.value = true
                    },
                    isEnabled = true
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                CustomTextButton(
                    text = "データ削除",
                    onButtonClicked = {
                        isShowConfirmationWithdrawalDialog.value = true
                    },
                    isEnabled = true
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
        // ログアウト確認ダイアログ
        if(isShowConfirmationLogoutDialog.value) {
            CustomDestructiveAlertDialog(
                title = "",
                text = "ログアウトしますか？",
                okText = "ログアウト",
                onOkButtonClicked = {
                    isShowConfirmationLogoutDialog.value = false
                    viewModel.handleLogout()
                },
                onCancelButtonClicked = {
                    isShowConfirmationLogoutDialog.value = false
                },
            )
        }
        // データ削除確認ダイアログ
        if(isShowConfirmationWithdrawalDialog.value) {
            CustomDestructiveAlertDialog(
                title = "",
                text = "アカウントを削除しますか？",
                okText = "削除",
                onOkButtonClicked = {
                    isShowConfirmationWithdrawalDialog.value = false
                    viewModel.handleWithdrawal()
                },
                onCancelButtonClicked = {
                    isShowConfirmationWithdrawalDialog.value = false
                },
            )
        }
    }
}
@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfNotConfirmEmailScreen() {
    NotConfirmEmailScreen(viewModel = ViewModel())
}