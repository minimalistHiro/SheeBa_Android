package com.hiroki.sheeba.app

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object EntryScreen: Screen()
    object CompulsionEntryScreen: Screen()
    object SetUpUsernameScreen: Screen()
    object SetUpEmailScreen: Screen()
    object ConfirmEmailScreen: Screen()
    object NotConfirmEmailScreen: Screen()
    object LoginScreen: Screen()
    object SendEmailScreen: Screen()
    object ContentScreen: Screen()
    object SendPayScreen: Screen()
    object GetPointScreen: Screen()
}

object PostOfficeAppRouter {

    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.EntryScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}