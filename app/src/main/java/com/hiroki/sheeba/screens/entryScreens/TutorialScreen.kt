package com.hiroki.sheeba.screens.entryScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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

data class Tutorial(val title: String, val text: String)

@ExperimentalMaterial3Api
@Composable
fun TutorialScreen(viewModel: ViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    var page = remember {
        mutableStateOf(1)
    }                                                   // ページ数
    var isAgree = remember {
        mutableStateOf(false)
    }                                                   // 同意済みか否か
    val tutorials = listOf(
        Tutorial(title = Setting.termsOfServiceTitle1, text = Setting.termsOfServiceArticle1),
        Tutorial(title = Setting.termsOfServiceTitle2, text = Setting.termsOfServiceArticle2),
        Tutorial(title = Setting.termsOfServiceTitle3, text = Setting.termsOfServiceArticle3),
        Tutorial(title = Setting.termsOfServiceTitle4, text = Setting.termsOfServiceArticle4),
        Tutorial(title = Setting.termsOfServiceTitle5, text = Setting.termsOfServiceArticle5),
        Tutorial(title = Setting.termsOfServiceTitle6, text = Setting.termsOfServiceArticle6),
        Tutorial(title = Setting.termsOfServiceTitle7, text = Setting.termsOfServiceArticle7),
        Tutorial(title = Setting.termsOfServiceTitle8, text = Setting.termsOfServiceArticle8),
        Tutorial(title = Setting.termsOfServiceTitle9, text = Setting.termsOfServiceArticle9),
        Tutorial(title = Setting.termsOfServiceTitle10, text = Setting.termsOfServiceArticle10),
    )

    // 初期化処理
    viewModel.init()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.sheebaYellow)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(Setting.tutorialLastPage != page.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {page.value = Setting.tutorialLastPage},
                        ) {
                            Text(
                                text = "スキップ",
                                fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height((screenHeight / 3).dp))

                    Text(
                        text = Setting.tutorialText(page = page.value),
                        fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Spacer(modifier = Modifier.height((screenHeight / 30).dp))

                    Text(
                        text = Setting.termsOfServiceTitle,
                        fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height((screenHeight / 50).dp))

                    //　利用規約本文
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((screenHeight / 1.7).dp)
                            .background(colorResource(id = R.color.chatLogBackground))
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = Setting.termsOfServiceExplanation,
                            fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Start,
                        )

                        tutorials.forEach { tutorial ->
                            Text(
                                text = tutorial.title,
                                fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = tutorial.text,
                                fontSize = with(LocalDensity.current) { (15 / fontScale).sp },
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                ),
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height((screenHeight / 1.3).dp))

                if(Setting.tutorialLastPage != page.value) {
                    CustomCapsuleButton(
                        text = "次へ",
                        onButtonClicked = {
                            page.value += 1
                        },
                        isEnabled = true
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    ) {
                        Checkbox(
                            checked = isAgree.value,
                            onCheckedChange = { isAgree.value = it },
                            colors = CheckboxDefaults.colors(Color.Blue)
                        )
                        Text(
                            text = "同意します",
                            fontSize = with(LocalDensity.current) { (25 / fontScale).sp },
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height((screenHeight / 50).dp))

                    CustomCapsuleButton(
                        text = "始める",
                        onButtonClicked = {
                            PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
                        },
                        isEnabled = isAgree.value
                    )
                }
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfTutorialScreen() {
    TutorialScreen(viewModel = ViewModel())
}