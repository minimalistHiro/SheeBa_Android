package com.hiroki.sheeba.screens.mapScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hiroki.sheeba.viewModel.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.hiroki.sheeba.R
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.model.Stores
import com.hiroki.sheeba.screens.components.CustomCapsuleButton
import com.hiroki.sheeba.screens.components.CustomImagePicker
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.Setting

data class PinItem(
    val uid: String,
    val coordinate: LatLng,
    val buttonSize: Float,
    val imageUrl: String,
    val storeName: String,
)
@ExperimentalMaterial3Api
@Composable
fun MapScreen(viewModel: ViewModel, navController: NavHostController) {
    val context = LocalContext.current
//    var storeUsers by remember { mutableStateOf(listOf<ChatUser>()) }
//    var pinItems by remember { mutableStateOf(listOf<PinItem>()) }
    var region by remember { mutableStateOf(
        com.google.android.gms.maps.model.LatLng(
            35.83306,
            139.69230
        )
    ) }
    var selectedStoreUid by remember { mutableStateOf("") }
    var isShowStoreInfo by remember { mutableStateOf(false) }
    val defaultButtonSize = 70f

//    LaunchedEffect(Unit) {
//        viewModel.fetchAllStoreUsers { users ->
//            storeUsers = users
//            fetchPinItems(storeUsers) { pins ->
//                pinItems = pins
//            }
//        }
//        fetchAllStoreUsers(viewModel) { pins ->
//            pinItems = pins
//        }
//    }

    viewModel.fetchAllStoreUsersForMap()

    Box(modifier =  Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(region, 16f)
            },
//            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            viewModel.pinItems.forEach { item ->
                item?.let {
                    // ピン
                    Marker(
                        state = MarkerState(position = item.coordinate),
                        onClick = {
                            isShowStoreInfo = true
                            selectedStoreUid = item.uid
                            viewModel.store.value = viewModel.stores.find { it?.uid == selectedStoreUid }
                            viewModel.navStoreDetailScreen = NavStoreDetailScreen.MapScreen
                            true
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
//                    icon = painterResource(R.drawable.baseline_location_pin_24)
                    )
                }
            }
        }

        Column {
            Spacer(modifier = Modifier.weight(1f))

            if (isShowStoreInfo) {
                StoreInfoOverlay(
                    onClose = { isShowStoreInfo = false },
//                    user = viewModel.storeUsers.find { it?.uid == selectedStoreUid },
                    store = viewModel.store.value,
                    navController = navController,
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun StoreInfoOverlay(onClose: () -> Unit, store: Stores?, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
            .shadow(5.dp, RoundedCornerShape(20.dp))
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }

            // Profile Image and Username
            store?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    CustomImagePicker(
                        size = 80,
                        model = it.profileImageUrl,
                        isAlpha = false,
                        conditions = it.profileImageUrl != "") {}

//                    Image(
//                        painter = rememberCoilPainter(request = it.profileImageUrl),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .size(50.dp)
//                            .clip(CircleShape)
//                            .background(Color.Gray)
//                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = it.storename,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            CustomCapsuleButton(
                text = "詳細を見る",
                onButtonClicked = {navController.navigate(Setting.storeDetailScreen)},
                isEnabled = true,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 全店舗ユーザーを取得
 *
 * @param viewModel ViewModel
 * @param onResult 取得結果
 * @return なし
 */
fun fetchAllStoreUsers(viewModel: ViewModel, onResult: (List<PinItem>) -> Unit) {
    var pinItems = mutableListOf<PinItem>()

    FirebaseFirestore
        .getInstance()
        .collection(FirebaseConstants.users)
        .get()
        .addOnSuccessListener { documents ->
            for(document in documents) {
                document.toObject(ChatUser::class.java)?.let {
                    if(it.isStore && it.isEnableScan) {

                    }
                }
            }
        }
        .addOnFailureListener { exception ->
            viewModel.handleError(title = "", text = Setting.failureFetchUser, exception = exception)
        }
    onResult(pinItems)
}

/**
 * マップのピンを取得
 *
 * @param storeUsers 取得した店舗情報
 * @param onResult 取得結果（ピンの情報）
 * @return なし
 */
fun fetchPinItems(storeUsers: List<ChatUser>, onResult: (List<PinItem>) -> Unit) {
    val pinItems = storeUsers.mapNotNull { user ->
        if (user.pointX.isNotEmpty() && user.pointY.isNotEmpty()) {
            PinItem(
                uid = user.uid,
                coordinate = LatLng(
                    user.pointY.toDouble(),
                    user.pointX.toDouble()
                ),
                buttonSize = 70f,
                imageUrl = user.profileImageUrl,
                storeName = user.username,
            )
        } else {
            null
        }
    }
    Log.d("Pin", pinItems.count().toString())
    onResult(pinItems)
}