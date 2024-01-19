package com.hiroki.sheeba.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.hiroki.sheeba.screens.ContentScreen
import com.hiroki.sheeba.screens.entryScreens.EntryScreen
import com.hiroki.sheeba.screens.loginScreens.LoginScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpUsernameScreen
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun PostOfficeApp() {
    val viewModel = ViewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
//        viewModel.fetchCurrentUser()
        Crossfade(targetState = PostOfficeAppRouter.currentScreen) { currentState ->
            when(currentState.value) {
                is Screen.EntryScreen -> {
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        EntryScreen(viewModel = viewModel)
                    } else {
                        ContentScreen(viewModel = viewModel)
                    }
                }
                is Screen.SetUpUsernameScreen -> {
                    SetUpUsernameScreen(viewModel = viewModel)
                }
                is Screen.SetUpEmailScreen -> {
                    SetUpEmailScreen(viewModel = viewModel)
                }
                is Screen.LoginScreen -> {
                    LoginScreen(viewModel = viewModel)
                }
                is Screen.ContentScreen -> {
                    ContentScreen(viewModel = viewModel)
                }
            }
        }
//        EntryScreen()
    }
}