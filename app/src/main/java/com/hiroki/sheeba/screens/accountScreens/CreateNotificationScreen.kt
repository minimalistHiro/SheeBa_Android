package com.hiroki.sheeba.screens.accountScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.hiroki.sheeba.R
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.screens.components.CustomAlertDialog
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomRectImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.screens.components.InputTextBox
import com.hiroki.sheeba.screens.components.InputTextField
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.FirebaseConstants.address
import com.hiroki.sheeba.util.FirebaseConstants.age
import com.hiroki.sheeba.util.FirebaseConstants.email
import com.hiroki.sheeba.util.FirebaseConstants.uid
import com.hiroki.sheeba.util.FirebaseConstants.username
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel
import java.net.URL

@ExperimentalMaterial3Api
@Composable
fun CreateNotificationScreen(viewModel: ViewModel, navController: NavHostController) {
    var title by remember { mutableStateOf("") }         // タイトル
    var url by remember { mutableStateOf("") }          // URL
    var text by remember { mutableStateOf("") }          // 本文
    var imageUri by remember { mutableStateOf<Uri?>(null) }// 画像URI
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            imageUri = uri
        }
    )
    val isShowSuccessCreateDialog = remember {
        mutableStateOf(false)
    }                                                           // 作成完了ダイアログ

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTopAppBar(
                    title = "店舗お知らせを作成",
                    color = colorResource(id = R.color.sheebaYellow),
                    onButtonClicked = {
                        navController.navigate(Setting.accountScreen)
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                InputTextField(
                    label = "タイトル",
                    onTextSelected = {
                        title = it
                    },
                )

                Spacer(modifier = Modifier.height(40.dp))

                InputTextField(
                    label = "URL",
                    onTextSelected = {
                        url = it
                    },
                )

                Spacer(modifier = Modifier.height(40.dp))

                InputTextBox(
                    label = "本文",
                    onTextSelected = {
                        text = it
                    },
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 画像
                CustomRectImagePicker(
                    size = 240,
                    model = imageUri,
                    isAlpha = false,
                    text = "画像を選択してください",
                    conditions = (imageUri != null)) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                CustomCapsuleButton(
                    text = "送信",
                    onButtonClicked = {
                        if (imageUri != null) {
                            // 画像がある場合
                            val storageRef = FirebaseStorage.getInstance().reference.child(title)

                            storageRef
                                .putFile(imageUri!!)
                                .continueWithTask { task ->
                                    storageRef.downloadUrl
                                        .addOnSuccessListener {
                                            val data = hashMapOf<String, Any>(
                                                FirebaseConstants.title to title,
                                                FirebaseConstants.text to text,
                                                FirebaseConstants.isRead to false,
                                                FirebaseConstants.url to url,
                                                FirebaseConstants.imageUrl to it.toString(),
                                                FirebaseConstants.timestamp to Timestamp.now(),
                                            )
                                            // お知らせを保存
                                            for (user in viewModel.allUsersContainSelf) {
                                                viewModel.persistNotification(document1 = user.uid, document2 = title, data = data)
                                            }
                                        }
                                        .addOnFailureListener {
                                            viewModel.handleError(title = "", text = Setting.failurePersistImage, exception = it)
                                        }
                                }
                                .addOnFailureListener {
                                    viewModel.handleError(title = "", text = Setting.failurePersistImage, exception = it)
                                }
                        } else {
                            // 画像がない場合
                            val data = hashMapOf<String, Any>(
                                FirebaseConstants.title to title,
                                FirebaseConstants.text to text,
                                FirebaseConstants.isRead to false,
                                FirebaseConstants.url to url,
                                FirebaseConstants.imageUrl to "",
                                FirebaseConstants.timestamp to Timestamp.now(),
                            )
                            // お知らせを保存
                            for (user in viewModel.allUsersContainSelf) {
                                viewModel.persistNotification(document1 = user.uid, document2 = title, data = data)
                            }
                        }
                        isShowSuccessCreateDialog.value = true
                    },
                    isEnabled = true,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(100.dp))
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
        if(isShowSuccessCreateDialog.value) {
            CustomAlertDialog(
                title = "",
                text = "お知らせを作成しました。") {
                isShowSuccessCreateDialog.value = false
                navController.navigate(Setting.accountScreen)
            }
        }
    }
}