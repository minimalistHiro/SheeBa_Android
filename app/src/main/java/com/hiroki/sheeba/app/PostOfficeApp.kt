package com.hiroki.sheeba.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hiroki.sheeba.screens.entryScreens.EntryScreen
import com.hiroki.sheeba.screens.homeScreens.HomeScreen
import com.hiroki.sheeba.screens.loginScreens.LoginScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpEmailScreen
import com.hiroki.sheeba.screens.signUpScreens.SetUpUsernameScreen
import com.hiroki.sheeba.viewModel.ViewModel

@ExperimentalMaterial3Api
@Composable
fun PostOfficeApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = PostOfficeAppRouter.currentScreen) { currentState ->
            when(currentState.value) {
                is Screen.EntryScreen -> {
                    EntryScreen()
                }
                is Screen.SetUpUsernameScreen -> {
                    SetUpUsernameScreen(viewModel = ViewModel())
                }
                is Screen.SetUpEmailScreen -> {
                    SetUpEmailScreen(viewModel = ViewModel())
                }
                is Screen.LoginScreen -> {
                    LoginScreen(viewModel = ViewModel())
                }
                is Screen.HomeScreen -> {
                    HomeScreen(viewModel = ViewModel())
                }
            }
        }
//        EntryScreen()
    }
}