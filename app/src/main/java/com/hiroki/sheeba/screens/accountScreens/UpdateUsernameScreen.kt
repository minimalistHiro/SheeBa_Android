package com.hiroki.sheeba.screens.accountScreens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.hiroki.sheeba.R
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputTextField
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun UpdateUsernameScreen(viewModel: ViewModel, navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val isShowSuccessUpdateDialog = remember {
        mutableStateOf(false)
    }                                                       // 更新完了ダイアログ

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTopAppBar(
                    title = "ユーザー名を変更",
                    onButtonClicked = {
                        navController.navigate(Setting.accountScreen)
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                InputTextField(
                    label = "ユーザー名",
                    onTextSelected = {
                        viewModel.onSignUpEvent(SignUpUIEvent.UsernameChange(it))
                    },
//                    errorStatus = viewModel.signUpUIState.value.usernameError
                )

                Spacer(modifier = Modifier.height((screenHeight / 5).dp))

                CustomCapsuleButton(
                    text = "ユーザー名を変更",
                    onButtonClicked = {
                        viewModel.progress.value = true
                        //　更新データ
                        val userData = hashMapOf<String, Any>(
                            FirebaseConstants.username to viewModel.signUpUIState.value.username,
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection(FirebaseConstants.users)
                            .document(viewModel.currentUser.value.uid)
                            .update(userData)
                            .addOnSuccessListener {
                                isShowSuccessUpdateDialog.value = true
                                viewModel.progress.value = false
                            }
                            .addOnFailureListener {
                                viewModel.handleError(title = "", text = Setting.failureUpdateUser, exception = it)
                            }
                    },
//                    isEnabled = viewModel.signUpUsernamePassed.value
                    isEnabled = (!viewModel.signUpUIState.value.username.isEmpty())
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
        // ダイアログ
        if(isShowSuccessUpdateDialog.value) {
            CustomAlertDialog(
                title = "",
                text = "変更しました。") {
                isShowSuccessUpdateDialog.value = false
                navController.navigate(Setting.accountScreen)
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfUpdateUsernameScreen() {
    UpdateUsernameScreen(viewModel = ViewModel(), navController = rememberNavController())
}