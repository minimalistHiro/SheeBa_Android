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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.model.ChatUserItem
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomDropdownMenu
import com.hiroki.sheeba.screens.components.CustomIcon
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputTextField
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun SetUpUsernameScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(ChatUserItem.ages[0]) }      // 選択値

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTopAppBar(
                    title = "新規アカウントを作成",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                Text(
                    text = "トップ画像（任意）",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height((screenHeight / 50).dp))

                CustomIcon()
//            AsyncImage(
//                model = "",
//                contentDescription = null,
//            )

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                InputTextField(
                    label = "ユーザー名",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.UsernameChange(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.usernameError
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                CustomDropdownMenu(
                    items = ChatUserItem.ages,
                    text = "年代を選択してください",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.AgeChange(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.ageError,
                )

                Spacer(modifier = Modifier.height((screenHeight / 25).dp))

                CustomDropdownMenu(
                    items = ChatUserItem.addresses,
                    text = "住所を選択してください",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.AddressChange(it))
                    },
                    errorStatus = viewModel.signUpUIState.value.addressError,
                )

                Spacer(modifier = Modifier.height((screenHeight / 10).dp))

                CustomCapsuleButton(
                    text = "次へ",
                    onButtonClicked = {
                        PostOfficeAppRouter.navigateTo(Screen.SetUpEmailScreen)
                    },
                    isEnabled = viewModel.signUpUsernameValidationPassed.value
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
fun DefaultPreviewOfSetUpUsernameScreen() {
    SetUpUsernameScreen(viewModel = ViewModel())
}