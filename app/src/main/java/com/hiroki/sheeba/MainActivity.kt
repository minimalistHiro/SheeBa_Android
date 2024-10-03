package com.hiroki.sheeba

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.hiroki.sheeba.app.PostOfficeApp
import com.hiroki.sheeba.databinding.ActivityMapsBinding

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostOfficeApp()
            RequestCameraPermission {
                Log.d("OnPermissionGranted", "Camera")
            }
        }
    }
}

/**
 * Cameraパーミッションをアプリ起動時にリクエストする
 *
 * @param onPermissionGranted パーミッション作成時の実行処理
 * @return なし
 */
@Composable
private fun Activity.RequestCameraPermission(
    onPermissionGranted: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            finish()
        } else {
            onPermissionGranted()
        }
    }
    LaunchedEffect(key1 = Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }
}

/**
 * QRコード取得UseCase
 *
 * @param onQrDetected
 * @return ImageAnalysis.Analyzer
 */
class QrCodeAnalyzer(
    private val onQrDetected: (Barcode) -> Unit,
) : ImageAnalysis.Analyzer {
    private val qrScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    // 画像内のQRコードを取得するための変数
    private val qrScanner = BarcodeScanning.getClient(qrScannerOptions)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        // カメラから上手く画像を取得することができているとき
        if (mediaImage != null) {
            // CameraXで取得した画像をInputImage形式に変換する
            val adjustedImage =
                InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            qrScanner.process(adjustedImage)
                .addOnSuccessListener {
                    if (it.isNotEmpty()) {
                        onQrDetected(it[0])
                    }
                }
                .addOnCompleteListener { image.close() }
        }
    }
}
