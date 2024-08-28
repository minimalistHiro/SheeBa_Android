package com.hiroki.sheeba.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.ChatUser
import androidx.navigation.NavController
import androidx.navigation.Navigation

@ExperimentalMaterial3Api
@Composable
fun CustomRankingCard(user: ChatUser) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Box(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .shadow(5.dp, shape = RoundedCornerShape(size = 30.dp))
            .clip(RoundedCornerShape(size = 30.dp))
            .background(Color.White)
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${user.ranking}位：",
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = user.username,
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アイコン画像
                CustomImagePicker(
                    size = 100,
                    model = user.profileImageUrl,
                    isAlpha = false,
                    conditions = user.profileImageUrl != "") {}

                Spacer(modifier = androidx.compose.ui.Modifier.width((screenWidth / 10).dp))

                Text(
                    text = user.money,
                    fontSize = with(LocalDensity.current) { (40 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "pt",
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CustomStoreCard(user: ChatUser, isGetPoint: Boolean, onButtonClicked: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    Box(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .shadow(5.dp, shape = RoundedCornerShape(size = 30.dp))
            .clip(RoundedCornerShape(size = 30.dp))
            .background(Color.White)
            .fillMaxWidth()
            .height(110.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        TextButton(
            onClick = {
                onButtonClicked.invoke()
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width((screenWidth / 20).dp))

                // チェックマーク
                if(isGetPoint) {
                    Icon(
                        modifier = Modifier
                            .widthIn(20.dp)
                            .heightIn(20.dp),
                        painter = painterResource(id = R.drawable.baseline_check_circle_24),
                        contentDescription = "",
                        tint = Color.Blue,
                    )
                } else {
                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.width((screenWidth / 30).dp))

                // アイコン画像
                if (isGetPoint) {
                    CustomImagePicker(
                        size = 70,
                        model = user.profileImageUrl,
                        isAlpha = false,
                        conditions = user.profileImageUrl != ""
                    ) {}
                } else {
                    CustomImagePicker(
                        size = 70,
                        model = user.profileImageUrl,
                        isAlpha = true,
                        conditions = user.profileImageUrl != ""
                    ) {}
                }

                Spacer(modifier = Modifier.width((screenWidth / 20).dp))

                Text(
                    text = user.username,
                    fontSize = with(LocalDensity.current) { (20 / fontScale).sp },
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                    ),
                    textAlign = TextAlign.Center,
                    color = if(isGetPoint) Color.Black else Color.Gray,
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomRankingCard() {
    CustomRankingCard(user = ChatUser())
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCustomStoreCard() {
    CustomStoreCard(user = ChatUser(), isGetPoint = true, onButtonClicked = {})
}