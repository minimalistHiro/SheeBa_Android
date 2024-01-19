package com.hiroki.sheeba.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomAlertDialog(title: String, text: String, onButtonClicked: () -> Unit) {
//    val openDialog = remember { mutableStateOf(true) }

//    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = title)
            },
            text = {
                Text(text)
            },
            confirmButton = {
                TextButton(
                    onClick = {
//                        openDialog.value = false
                        onButtonClicked.invoke()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = null
        )
//    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomAlertDialog() {
    CustomAlertDialog(
        title = "テストエラー",
        text = "情報の取得に失敗しました。",
        onButtonClicked = {},
    )
}
