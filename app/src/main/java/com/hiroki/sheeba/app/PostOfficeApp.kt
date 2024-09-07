package com.hiroki.sheeba.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.hiroki.sheeba.screens.ContentScreen
import com.hiroki.sheeba.screens.cameraScreens.GetPointScreen
import com.hiroki.sheeba.screens.entryScreens.EntryScreen
import com.hiroki.sheeba.screens.entryScreens.TutorialScreen
import com.hiroki.sheeba.screens.homeScreens.SendPayScreen
import com.hiroki.sheeba.screens.loginScreens.LoginScreen
import com.hiroki.sheeba.screens.loginScreens.SendEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.ConfirmEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.NotConfirmEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpUsernameScreen
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun PostOfficeApp() {
    val viewModel = ViewModel()
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
//        viewModel.fetchCurrentUser()
        Crossfade(targetState = PostOfficeAppRouter.currentScreen, label = "") { currentState ->
            when(currentState.value) {
                is Screen.TopScreen -> {
                    if(FirebaseAuth.getInstance().currentUser == null) {
                        TutorialScreen(viewModel = viewModel)
                    } else {
                        if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                            ContentScreen(viewModel = viewModel, navController)
                        } else {
                            NotConfirmEmailScreen(viewModel = viewModel)
//                            TutorialScreen(viewModel = viewModel)
                        }
                    }
                }
                is Screen.EntryScreen -> {
                    EntryScreen(viewModel = viewModel)
                }
                is Screen.TutorialScreen -> {
                    TutorialScreen(viewModel = viewModel)
                }
                is Screen.SetUpUsernameScreen -> {
                    SetUpUsernameScreen(viewModel = viewModel)
                }
                is Screen.SetUpEmailScreen -> {
                    SetUpEmailScreen(viewModel = viewModel)
                }
                is Screen.ConfirmEmailScreen -> {
                    ConfirmEmailScreen(viewModel = viewModel)
                }
                is Screen.NotConfirmEmailScreen -> {
                    NotConfirmEmailScreen(viewModel = viewModel)
                }
                is Screen.LoginScreen -> {
                    LoginScreen(viewModel = viewModel)
                }
                is Screen.SendEmailScreen -> {
                    SendEmailScreen(viewModel = viewModel)
                }
                is Screen.ContentScreen -> {
                    ContentScreen(viewModel = viewModel, navController)
                }
                is Screen.SendPayScreen -> {
                    SendPayScreen(viewModel = viewModel, navController)
                }
                is Screen.GetPointScreen -> {
                    GetPointScreen(viewModel = viewModel)
                }
            }
        }
    }
}