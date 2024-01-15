package com.hiroki.sheeba.data

sealed class SignUpUIEvent {
    data class EmailChange(val email: String): SignUpUIEvent()
    data class PasswordChange(val password: String): SignUpUIEvent()
    data class UsernameChange(val username: String): SignUpUIEvent()
    data class AgeChange(val age: String): SignUpUIEvent()
    data class AddressChange(val address: String): SignUpUIEvent()

    object SignUpButtonClicked: SignUpUIEvent()
}
