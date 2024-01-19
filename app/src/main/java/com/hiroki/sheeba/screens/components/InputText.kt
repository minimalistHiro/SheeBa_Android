package com.hiroki.sheeba.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.ChatUserItem

@ExperimentalMaterial3Api
@Composable
fun InputTextField(label: String, onTextSelected: (String) -> Unit, errorStatus: Boolean) {
    val textValue = remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        label = { Text(text = label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            cursorColor = Color.Black,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextSelected(it)
        },
        isError = !errorStatus,
    )
}

@ExperimentalMaterial3Api
@Composable
fun InputPasswordTextField(label: String, onTextSelected: (String) -> Unit, errorStatus: Boolean) {
    val localFocusManager = LocalFocusManager.current
    val textValue = remember {
        mutableStateOf("")
    }
    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        label = { Text(text = label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            cursorColor = Color.Black,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true,
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        maxLines = 1,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextSelected(it)
        },
        trailingIcon =  {
            val iconImage = if(passwordVisible.value) {
                ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
            } else {
                ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
            }

            val description = if(passwordVisible.value) {
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !errorStatus,
    )
}

@ExperimentalMaterial3Api
@Composable
fun CustomDropdownMenu(items: List<String>, text: String, onTextSelected: (String) -> Unit, errorStatus: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }      // 選択値

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .padding(horizontal = 15.dp)
            ,
            text = if(selectedItem == "" || selectedItem == " ") {
                text
            } else {
                selectedItem
            },
            style = TextStyle(
                fontSize = 17.sp,
                fontStyle = FontStyle.Normal,
            ),
            textAlign = TextAlign.Start,
            color = if(errorStatus) {
                Color.Black
            } else {
                Color.Red
            },
        )

        Box(modifier = Modifier) {
            IconButton(
                onClick = {
                    //クリックした時(状態を切り替える)
                    expanded = !expanded
                    // 初回クリックで、選択値を初期化
                    onTextSelected(" ")
                },
                modifier = Modifier
                    .padding(0.dp, 0.dp)
                    .height(25.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = "",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedItem = item
                            onTextSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    CustomDivider(
        color = if(errorStatus) {
            Color.Black
        } else {
            Color.Red
        }
    )
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfInputTextField() {
    InputTextField(label = "メールアドレス", onTextSelected = {}, errorStatus = true)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfInputPasswordTextField() {
    InputPasswordTextField(label = "パスワード", onTextSelected = {}, errorStatus = true)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomDropdownMenu() {
    CustomDropdownMenu(items = ChatUserItem.ages, text = "年代を選択してください", onTextSelected = {}, errorStatus = true)
}