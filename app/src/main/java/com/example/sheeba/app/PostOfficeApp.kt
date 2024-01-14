package com.example.sheeba.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sheeba.screens.entryScreens.EntryScreen
import com.example.sheeba.screens.homeScreens.HomeScreen
import com.example.sheeba.screens.loginScreens.LoginScreen
import com.example.sheeba.screens.signUpScreens.SetUpEmailScreen
import com.example.sheeba.screens.signUpScreens.SetUpUsernameScreen
import com.example.sheeba.viewModel.ViewModel

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
                    HomeScreen()
                }
            }
        }
        EntryScreen()
    }
}