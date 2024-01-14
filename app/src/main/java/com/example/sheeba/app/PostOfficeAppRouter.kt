package com.example.sheeba.app

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object EntryScreen: Screen()
    object SetUpUsernameScreen: Screen()
    object SetUpEmailScreen: Screen()
    object LoginScreen: Screen()
    object HomeScreen: Screen()
}

object PostOfficeAppRouter {

    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.EntryScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}