package com.hiroki.sheeba.data

sealed class LoginUIEvent {
    data class EmailChange(val email: String): LoginUIEvent()
    data class PasswordChange(val password: String): LoginUIEvent()

    object LoginButtonClicked: LoginUIEvent()
}