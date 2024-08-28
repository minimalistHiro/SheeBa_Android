package com.hiroki.sheeba.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hiroki.sheeba.R

@ExperimentalMaterial3Api
@Composable
fun CustomIcon(size: Int, isAlpha: Boolean) {
    Icon(
        modifier = Modifier
            .widthIn((size / 2).dp)
            .heightIn((size / 2).dp),
        painter = painterResource(id = R.drawable.baseline_person_24),
        contentDescription = "",
        tint = Color.Black.copy(alpha = if(isAlpha) 0.3F else 1.0F),
    )
}

@ExperimentalMaterial3Api
@Composable
fun CustomAsyncImage(size: Int, model: Any?, isAlpha: Boolean) {
    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = Modifier
            .width(size.dp)
            .height(size.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        alpha = if(isAlpha) 0.3F else 1.0F,
    )
}

@ExperimentalMaterial3Api
@Composable
fun CustomImagePicker(size: Int, model: Any?, conditions: Boolean, isAlpha: Boolean, onButtonClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .border(2.dp, if(isAlpha) Color.Gray else Color.Black, CircleShape)
            .clip(CircleShape)
            .background(Color(R.color.chatLogBackground))
    ) {
        IconButton(
            modifier = Modifier
                .widthIn(size.dp)
                .heightIn(size.dp),
            onClick = {
                onButtonClicked.invoke()
            },
        ) {
            if(conditions) {
                CustomAsyncImage(size = size, model = model, isAlpha = isAlpha)
            } else {
                CustomIcon(size = size, isAlpha = isAlpha)
            }
        }
    }
}

@Composable
fun IconArc(size: Int) {
    Canvas(
        modifier = Modifier,
    ) {
        drawArc(
            color = Color.Black,
            startAngle = 20F,
            sweepAngle = 160F,
            useCenter = true,
            size = Size(width = size.toFloat(), height = size.toFloat()),
        )
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfIcon() {
    CustomIcon(size = 120, isAlpha = false)
}