package com.example.sheeba.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@ExperimentalMaterial3Api
@Composable
fun CustomTopAppBar(title: String, onButtonClicked: () -> Unit = {}) {
    TopAppBar(
        title = { Text(
            text = title,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
            ),
        ) },
        navigationIcon = {
            IconButton(
                onClick = {
                    onButtonClicked.invoke()
                }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "")
            }
        },
    )
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomTopAppBar() {
    CustomTopAppBar(title = "タイトル")
}