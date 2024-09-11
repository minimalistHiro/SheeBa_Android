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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
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
import com.hiroki.sheeba.screens.components.CustomDestructiveAlertDialog
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomDoubleAlertDialog
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomListNav
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

enum class ExternalLink {
    PrivacyPolicy,
    OfficialSite,
}
@ExperimentalMaterial3Api
@Composable
fun AccountScreen(viewModel: ViewModel, padding: PaddingValues, navController: NavHostController) {
    val configuration = LocalConfiguration.current
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
    var isShowPrivacyPolicyDialog = remember {
        mutableStateOf(false)
    }                                   // プライバシーポリシー表示有無
    var externalLink = remember {
        mutableStateOf(ExternalLink.OfficialSite)
    }                                   // 外部リンクURL
    val uriHandler = LocalUriHandler.current                                // URL開示用変数

    // Screen開示処理
    viewModel.init()
    viewModel.fetchCurrentUser()
    viewModel.fetchRecentMessages()
    viewModel.fetchFriends()
    viewModel.fetchNotifications()
    viewModel.fetchStorePoints()

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                // トップ画像
                CustomImagePicker(
                    size = 120,
                    model = viewModel.currentUser.value?.profileImageUrl,
                    isAlpha = false,
                    conditions = (!viewModel.currentUser.value?.profileImageUrl.isNullOrEmpty())) {
                    navController.navigate(Setting.updateImageScreen)
                }

                Spacer(modifier = Modifier.height(15.dp))

                // ユーザー名
                if(viewModel.progress.value) {
                    "読み込み中..."
                } else {
                    viewModel.currentUser.value?.username
                }?.let {
                    Text(
                        text = it,
                        fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if(viewModel.progress.value) {
                        "読み込み中..."
                    } else {
                        "しばID：${viewModel.currentUser.value?.uid}"
                    },
                    fontSize = with(LocalDensity.current) { (12 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 15).dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 25.dp),
                        text = "設定",
                        fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    // 店舗お知らせを作成
                    viewModel.currentUser.value?.let {
                        if (it.isStoreOwner || it.isOwner) {
                            CustomListNav(text = "店舗お知らせを作成", color = Color.Blue) {
                                viewModel.fetchAllUsersContainSelf()
                                navController.navigate(Setting.createNotificationScreen)
                            }
                            CustomDivider(color = Color.Gray)
                        }
                    }

                    // ユーザー名を変更
                    CustomListNav(text = "ユーザー名を変更", color = Color.Black) {
                        navController.navigate(Setting.updateUsernameScreen)
                    }
                    CustomDivider(color = Color.Gray)

                    // 公式サイト
                    CustomListNav(text = "公式サイト", color = Color.Black) {
                        externalLink.value = ExternalLink.OfficialSite
                        isShowPrivacyPolicyDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)

                    // プライバシーポリシー
                    CustomListNav(text = "プライバシーポリシー", color = Color.Black) {
                        externalLink.value = ExternalLink.PrivacyPolicy
                        isShowPrivacyPolicyDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)

                    // ログアウト
                    CustomListNav(text = "ログアウト", color = Color.Red) {
                        isShowConfirmationLogoutDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)

                    // 退会する
                    CustomListNav(text = "退会する", color = Color.Red) {
                        isShowConfirmationWithdrawalDialog.value = true
                    }
                    CustomDivider(color = Color.Gray)

                    Spacer(modifier = Modifier.height((screenHeight / 10).dp))
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
        // プライバシーポリシー開示確認ダイアログ
        if(isShowPrivacyPolicyDialog.value) {
            CustomDoubleAlertDialog(
                title = "",
                text = "外部リンクに飛びます。よろしいですか？",
                okText = "はい",
                onOkButtonClicked = {
                    isShowPrivacyPolicyDialog.value = false

                    // 飛ぶ外部リンクを分ける
                    when(externalLink.value) {
                        ExternalLink.OfficialSite -> uriHandler.openUri(Setting.officialSiteURL)
                        ExternalLink.PrivacyPolicy -> uriHandler.openUri(Setting.privacyPolicyURL)
                    }
                },
                onCancelButtonClicked = {
                    isShowPrivacyPolicyDialog.value = false
                },
            )
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
        // 退会確認ダイアログ
        if(isShowConfirmationWithdrawalDialog.value) {
            CustomDestructiveAlertDialog(
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