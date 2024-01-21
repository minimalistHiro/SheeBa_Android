package com.hiroki.sheeba.screens.accountScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomDoubleAlertDialog
import com.hiroki.sheeba.screens.components.CustomIcon
import com.hiroki.sheeba.screens.components.CustomListNav
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun AccountScreen(viewModel: ViewModel, padding: PaddingValues, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var isShowConfirmationLogoutDialog = remember {
        mutableStateOf(false)
    }                                   // ログアウト確認ダイアログの表示有無
    var isShowConfirmationWithdrawalDialog = remember {
        mutableStateOf(false)
    }                                   // 退会確認ダイアログの表示有無
    var isShowSuccessWithdrawalDialog = remember {
        mutableStateOf(false)
    }                                   // 退会完了ダイアログの表示有無

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                CustomIcon()

                Spacer(modifier = Modifier.height(15.dp))

                if(viewModel.progress.value) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = viewModel.currentUser.value.username,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if(viewModel.progress.value) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = "しばID：${viewModel.currentUser.value.uid}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height((screenHeight / 15).dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 25.dp),
                        text = "設定",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    CustomListNav(text = "ユーザー名を変更", color = Color.Black) {
                        navController.navigate(Setting.updateUsernameScreen)
                    }
                    CustomDivider(color = Color.Gray)

                    CustomListNav(text = "ログアウト", color = Color.Red) {
                        isShowConfirmationLogoutDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)
                    CustomListNav(text = "退会する", color = Color.Red) {
                        isShowConfirmationWithdrawalDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)
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
                viewModel.isShowCompulsionLogoutDialog.value = false
                viewModel.handleLogout()
            }
        }
        // ログアウト確認ダイアログ
        if(isShowConfirmationLogoutDialog.value) {
            CustomDoubleAlertDialog(
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
        // 退会確認ダイアログ
        if(isShowConfirmationWithdrawalDialog.value) {
            CustomDoubleAlertDialog(
                title = "",
                text = "退会しますか？",
                okText = "退会",
                onOkButtonClicked = {
                    isShowConfirmationWithdrawalDialog.value = false
                    viewModel.handleWithdrawal()
                    isShowSuccessWithdrawalDialog.value = true
                },
                onCancelButtonClicked = {
                    isShowConfirmationWithdrawalDialog.value = false
                },
            )
        }
        // 退会完了ダイアログ
        if(isShowSuccessWithdrawalDialog.value) {
            CustomAlertDialog(
                title = "",
                text = "ご利用ありがとうございました。") {
                isShowSuccessWithdrawalDialog.value = false
                viewModel.handleLogout()
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfAccountScreen() {
    AccountScreen(
        viewModel = ViewModel(),
        padding = PaddingValues(20.dp),
        navController = rememberNavController()
    )
}