package com.hiroki.sheeba.data

import android.net.Uri

sealed class SignUpUIEvent {
    data class EmailChange(val email: String): SignUpUIEvent()
    data class PasswordChange(val password: String): SignUpUIEvent()
    data class Password2Change(val password2: String): SignUpUIEvent()
    data class ProfileImageUrlChange(val imageUri: Uri?): SignUpUIEvent()
    data class UsernameChange(val username: String): SignUpUIEvent()
    data class AgeChange(val age: String): SignUpUIEvent()
    data class AddressChange(val address: String): SignUpUIEvent()

    object SignUpButtonClicked: SignUpUIEvent()
}
