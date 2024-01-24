package com.hiroki.sheeba.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalMaterial3Api
@Composable
fun CustomCapsuleButton(text: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .padding(horizontal = 30.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            if(isEnabled) {
                Color.Black
            } else {
                Color.Gray
            }
        ),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled,
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                color =
                if(isEnabled) {
                    Color.Black
                } else {
                    Color.Gray
                },
                shape = RoundedCornerShape(50.dp),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomBorderCapsuleButton(text: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
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
            Text(text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomTextButton(text: String, onButtonClicked: () -> Unit, isEnabled: Boolean) {
    TextButton(
        onClick = {
            onButtonClicked.invoke()
        }
    ) {
        Text(text = text,
            fontSize = 18.sp,
            color = Color.Blue)
    }
}

@Composable
fun CustomDivider(color: Color) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            color = color,
            thickness = 1.dp
        )
    }
}

@Composable
fun CustomListNav(text: String, color: Color, onButtonClicked: () -> Unit) {
    TextButton(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .padding(horizontal = 25.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        colors = ButtonDefaults.textButtonColors(Color.White)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
            textAlign = TextAlign.Center,
            color = color,
        )
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomCapsuleButton() {
    CustomCapsuleButton(text = "Test", onButtonClicked = {}, isEnabled = false)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomBorderCapsuleButton() {
    CustomBorderCapsuleButton(text = "Test", onButtonClicked = {}, isEnabled = false)
}


@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomTextButton() {
    CustomTextButton(text = "Test", onButtonClicked = {}, isEnabled = false)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomDivider() {
    CustomDivider(color = Color.Gray)
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomListNav() {
    CustomListNav(text = "Test", color = Color.Black, onButtonClicked = {})
}