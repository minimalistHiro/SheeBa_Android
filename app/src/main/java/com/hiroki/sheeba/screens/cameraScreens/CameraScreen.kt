package com.hiroki.sheeba.screens.cameraScreens

//import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.common.Barcode
import com.hiroki.sheeba.R
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun CameraScreen(
    viewModel: ViewModel,
    padding: PaddingValues,
    navController: NavHostController,
    vararg useCases: UseCase,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // CameraX プレビューUseCase
            val previewUseCase = androidx.camera.core.Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            context.startCamera(
                lifecycleOwner = lifecycleOwner,
                previewUseCase,
                *useCases,
            )

            return@AndroidView previewView
        }
    )
}

@Composable
fun QrCodeOverlay(
    qrCode: Barcode,
    imageSize: android.util.Size = Setting.IMAGE_SIZE,
) {
    val qrBoundingBox = qrCode.boundingBox ?: return

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    with(LocalDensity.current) {
        val scaleFactor = (screenHeightDp * density) / imageSize.height
        val offsetX = (screenWidthDp - screenHeightDp * imageSize.width / imageSize.height) / 2 * density

        val qrCodeTopLeft = Offset(
            x = (qrBoundingBox.left.toFloat() + offsetX) * scaleFactor,
            y = qrBoundingBox.top.toFloat() * scaleFactor,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {

            drawRect(
                color = Color.White,
                topLeft = qrCodeTopLeft,
                size = Size(
                    width = (qrBoundingBox.right - qrBoundingBox.left) * scaleFactor,
                    height = (qrBoundingBox.bottom - qrBoundingBox.top) * scaleFactor,
                ),
                style = Stroke(width = 10f),
            )
        }
    }
}

@Composable
fun ErrorQRCodeOverlay() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.5F)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Red),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp),
                tint = Color.White.copy(alpha = 0.6F),
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.7F)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
//                        .wrapContentWidth(Alignment.Start)
                        .padding(horizontal = 15.dp)
                    ,
                    text = "誤ったQRコードがスキャンされました",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Normal,
                    ),
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * カメラのライフサイクルとViewのライフサイクルを紐づける
 *
 * @param lifecycleOwner ライフサイクルオーナー
 * @param useCases
 * @return なし
 */
private fun Context.startCamera(
    lifecycleOwner: LifecycleOwner,
    vararg useCases: UseCase,
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        // 背面カメラを使用するように設定
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, *useCases)
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }, ContextCompat.getMainExecutor(this))
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfCameraScreen() {
    CameraScreen(
        viewModel = ViewModel(),
        padding = PaddingValues(all = 20.dp),
        navController = rememberNavController(),
    )
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreviewOfErrorQRCodeOverlay() {
    ErrorQRCodeOverlay()
}