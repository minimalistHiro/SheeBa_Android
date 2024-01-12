//package com.example.sheeba.screens.components
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.ImageDecoder
//import android.media.Image
//import android.net.Uri
//import android.os.Build
//import android.text.Layout
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import com.example.sheeba.R
//
//@RequiresApi(Build.VERSION_CODES.P)
//@Composable
//fun ImagePicker(
//    context: Context,
//    onResult: (uri: Uri) -> Unit
//) {
//    var imageUri: Uri? by remember {
//        mutableStateOf(null)
//    }
//
//    var bitmap: Bitmap? by remember {
//        mutableStateOf(null)
//    }
//
//    val launcher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
//            if (uri == null) return@rememberLauncherForActivityResult
//            imageUri = uri
//            onResult(uri)
//        }
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(256.dp)
//                .background(Color.Gray)
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.primary,
//                ),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            imageUri?.let {
//                val source = ImageDecoder
//                    .createSource(
//                        context.contentResolver,
//                        it,
//                    )
//                bitmap = ImageDecoder.decodeBitmap(source)
//
//                bitmap?.let { bm ->
//                    Image(
//                        bitmap = bm.asImageBitmap(),
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//
//            if (imageUri == null) {
//                Text(
//                    color = Color.White,
//                    text = stringResource(id = R.string.image_picker_text)
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        OutlinedButton(
//            modifier = Modifier
//                .testTag("AddButton")
//                .fillMaxWidth(),
//            onClick = {
//                launcher.launch("image/*")
//            },
//        ) {
//            Text(
//                text = stringResource(id = R.string.select_image)
//            )
//        }
//    }
//}