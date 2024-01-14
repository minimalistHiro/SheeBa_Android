package com.example.sheeba.screens.entryScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sheeba.screens.components.CustomBorderCapsuleButton
import com.example.sheeba.screens.components.CustomCapsuleButton

@ExperimentalMaterial3Api
@Composable
fun EntryScreen(onButtonClicked: () -> Unit = {}) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height((screenHeight / 3).dp))

            Text(
                text = "SheeBa",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                style = TextStyle(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height((screenHeight / 5).dp))

            CustomBorderCapsuleButton(
                value = "アカウントを作成する",
                onButtonClicked = { onButtonClicked.invoke() },
                isEnabled = true
            )

            Spacer(modifier = Modifier.height((screenHeight / 20).dp))

            CustomCapsuleButton(
                value = "ログイン",
                onButtonClicked = { /*TODO*/ },
                isEnabled = true
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfEntryScreen() {
    EntryScreen()
}