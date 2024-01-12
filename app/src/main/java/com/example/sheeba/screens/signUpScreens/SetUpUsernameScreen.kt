package com.example.sheeba.screens.signUpScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sheeba.screens.components.CustomCapsuleButton
import com.example.sheeba.screens.components.CustomIcon
import com.example.sheeba.screens.components.InputTextField
import com.example.sheeba.util.Setting

@ExperimentalMaterial3Api
@Composable
fun SetUpUsernameScreen(onButtonClicked: () -> Unit = {}) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Setting.surfacePadding.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height((screenHeight / 10).dp))

            Text(
                text = "トップ画像（任意）",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height((screenHeight / 50).dp))

            CustomIcon()
//            AsyncImage(
//                model = "",
//                contentDescription = null,
//            )

            Spacer(modifier = Modifier.height((screenHeight / 20).dp))

            InputTextField(text = "test", label = "ユーザー名", errorStatus = false)

            Spacer(modifier = Modifier.height((screenHeight / 25).dp))

            InputTextField(text = "test", label = "年代", errorStatus = false)

            Spacer(modifier = Modifier.height((screenHeight / 25).dp))

            InputTextField(text = "test", label = "住所", errorStatus = false)

            Spacer(modifier = Modifier.height((screenHeight / 10).dp))

            CustomCapsuleButton(
                value = "次へ",
                onButtonClicked = { onButtonClicked.invoke() },
                isEnabled = true
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfSignUpScreen() {
    SetUpUsernameScreen()
}