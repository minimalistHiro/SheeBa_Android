package com.hiroki.sheeba.screens.entryScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun TutorialScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var page = remember {
        mutableStateOf(1)
    }                                                   // ページ数

    // 初期化処理
    viewModel.init()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.sheebaYellow)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height((screenHeight / 3).dp))

            Text(
                text = Setting.tutorialText(page = page.value),
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height((screenHeight / 7).dp))

            CustomCapsuleButton(
                text = "次へ",
                onButtonClicked = {
                    if(Setting.tutorialLastPage != page.value) {
                        page.value += 1
                    } else {
                        PostOfficeAppRouter.navigateTo(Screen.CompulsionEntryScreen)
                    }
                },
                isEnabled = true
            )
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfTutorialScreen() {
    TutorialScreen(viewModel = ViewModel())
}