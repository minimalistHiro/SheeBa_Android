package com.example.sheeba.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sheeba.R

@ExperimentalMaterial3Api
@Composable
fun CustomIcon() {
//    IconButton(
//        modifier = Modifier
//            .widthIn(100.dp)
//            .heightIn(100.dp),
//        onClick = {
//
//        },
//        colors = IconButtonDefaults.iconButtonColors(
//            containerColor = Color(R.string.chat_log_background_color),
//            contentColor = Color(R.string.chat_log_background_color),
//            disabledContainerColor = Color(R.string.chat_log_background_color),
//            disabledContentColor = Color(R.string.chat_log_background_color),
//        ),
//    ) {
//        Icon(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(60.dp),
//            painter = painterResource(id = R.drawable.baseline_person_24),
//            contentDescription = "",
//            tint = Color.Black,
//        )
//    }
    Button(
        modifier = Modifier
            .widthIn(120.dp)
            .heightIn(120.dp),
        onClick = {

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(R.color.chatLogBackground),
            contentColor = Color(R.color.chatLogBackground),
            disabledContainerColor = Color(R.color.chatLogBackground),
            disabledContentColor = Color(R.color.chatLogBackground),
        ),
        border = BorderStroke(
            width = 4.dp,
            color = Color.Black,
        ),
    ) {
        Icon(
            modifier = Modifier
                .widthIn(70.dp)
                .heightIn(70.dp),
            painter = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = "",
            tint = Color.Black,
        )
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfIcon() {
    CustomIcon()
}