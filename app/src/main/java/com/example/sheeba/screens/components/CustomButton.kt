package com.example.sheeba.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalMaterial3Api
@Composable
fun CustomCapsuleButton(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 30.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Black),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled,
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(50.dp),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomBorderCapsuleButton(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 30.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.White),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled,
        border = BorderStroke(
            width = 4.dp,
            color = Color.Black,
        )
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(50.dp),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomTextButton(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
    TextButton(
        onClick = {
            onButtonClicked.invoke()
        }
    ) {
        Text(text = value,
            fontSize = 18.sp,
            color = Color.Blue)
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomCapsuleButton() {
    CustomCapsuleButton(value = "Test", onButtonClicked = {}, isEnabled = false)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomBorderCapsuleButton() {
    CustomBorderCapsuleButton(value = "Test", onButtonClicked = {}, isEnabled = false)
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomTextButton() {
    CustomTextButton(value = "Test", onButtonClicked = {}, isEnabled = false)
}