package com.hiroki.sheeba.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomAlertDialog(
    title: String,
    text: String,
    onButtonClicked: () -> Unit
) {
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
                    onButtonClicked.invoke()
                }
            ) {
                Text(text = "OK",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        },
        dismissButton = null
    )
}

@Composable
fun CustomDoubleAlertDialog(
    title: String,
    text: String,
    okText: String,
    onOkButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
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
                    onOkButtonClicked.invoke()
                }
            ) {
                Text(text = okText,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancelButtonClicked.invoke()
                }
            ) {
                Text(text = "キャンセル")
            }
        }
    )
}

@Composable
fun CustomDestructiveAlertDialog(
    title: String,
    text: String,
    okText: String,
    onOkButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
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
                    onOkButtonClicked.invoke()
                }
            ) {
                Text(text = okText,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancelButtonClicked.invoke()
                }
            ) {
                Text(text = "キャンセル")
            }
        }
    )
}

@Composable
fun CustomDoubleTextAlertDialog(
    title: String,
    text: String,
    okText: String,
    cancelText: String,
    onOkButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
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
                    onOkButtonClicked.invoke()
                }
            ) {
                Text(text = okText,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancelButtonClicked.invoke()
                }
            ) {
                Text(text = cancelText,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    )
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

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomDoubleAlertDialog() {
    CustomDoubleAlertDialog(
        title = "テストエラー",
        text = "削除しますか？",
        okText = "削除",
        onOkButtonClicked = {},
        onCancelButtonClicked = {},
    )
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomDestructiveAlertDialog() {
    CustomDestructiveAlertDialog(
        title = "テストエラー",
        text = "削除しますか？",
        okText = "削除",
        onOkButtonClicked = {},
        onCancelButtonClicked = {},
    )
}
