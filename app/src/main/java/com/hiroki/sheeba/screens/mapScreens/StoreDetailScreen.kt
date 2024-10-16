package com.hiroki.sheeba.screens.mapScreens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivities
import androidx.core.app.ActivityCompat.startActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.screens.components.CustomDivider
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.screens.components.CustomTopAppBar
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel


enum class NavStoreDetailScreen {
    TodaysGetPointScreen,
    MapScreen,
}

@ExperimentalMaterial3Api
@Composable
fun StoreDetailScreen(viewModel: ViewModel, navController: NavController, storeUser: ChatUser?, nav: NavStoreDetailScreen) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenHeight = configuration.screenHeightDp
    val uriHandler = LocalUriHandler.current                                // URL開示用変数

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                CustomTopAppBar(
                    title = "",
                    color = Color.White,
                    onButtonClicked = {
                        when (nav) {
                            NavStoreDetailScreen.TodaysGetPointScreen -> navController.navigate(Setting.todaysGetPointScreen)
                            NavStoreDetailScreen.MapScreen -> navController.navigate(Setting.mapScreen)
                        }
                    }
                )

                Spacer(modifier = Modifier.height((screenHeight / 20).dp))

                // トップ画像
                CustomImagePicker(
                    size = 120,
                    model = viewModel.store.value?.profileImageUrl,
                    isAlpha = false,
                    conditions = (!viewModel.store.value?.profileImageUrl.isNullOrEmpty())) {}

                Spacer(modifier = Modifier.height(20.dp))

                // 店舗名
                viewModel.store.value?.storename?.let {
                    Text(
                        text = it,
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    viewModel.store.value?.let {
                        // ジャンル
                        CustomStoreDetailList(title = "ジャンル", text = it.genre, color = Color.Black) {}
                        CustomDivider(color = Color.Gray)

                        // 電話番号
                        CustomStoreDetailList(title = "電話番号", text = it.phoneNumber, color = Color.Blue) {
                            makePhoneCall(context, it.phoneNumber)
                        }
                        CustomDivider(color = Color.Gray)

                        // Webサイト
                        CustomStoreDetailList(title = "Webサイト", text = it.webURL, color = Color.Blue) {
                            uriHandler.openUri(it.webURL)
                        }
                        CustomDivider(color = Color.Gray)

                        // 紹介動画
                        CustomStoreDetailList(title = "紹介動画", text = it.movieURL, color = Color.Blue) {
                            uriHandler.openUri(it.movieURL)
                        }
                        CustomDivider(color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomStoreDetailList(title: String, text: String, color: Color, onButtonClicked: () -> Unit) {
    TextButton(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .padding(horizontal = 25.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        colors = ButtonDefaults.textButtonColors(Color.White)
    ) {
        Row {
            Text(
                text = title,
                fontSize = with(LocalDensity.current) { (18 / fontScale).sp },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = text,
                fontSize = with(LocalDensity.current) { (18 / fontScale).sp },
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                ),
                textAlign = TextAlign.Center,
                color = color,
            )
        }
    }
}

/**
 * 電話をかけるための関数
 *
 * @param context コンテキスト
 * @param phoneNumber 電話番号
 * @return なし
 */
fun makePhoneCall(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }

    // パーミッションが許可されているかを確認
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        Log.d("", "電話をかける際にエラーが発生しました。")
        return
    }

    context.startActivity(intent)
}