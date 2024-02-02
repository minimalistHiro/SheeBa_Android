package com.hiroki.sheeba.data

import android.net.Uri

data class SignUpUIState (
    var email: String = "",
    var password: String = "",
    var password2: String = "",
    var imageUri: Uri?,
    var username: String = "",
    var age: String = "",
    var address: String = "",
    var isConfirmEmail: Boolean = false,
    var isFirstLogin: Boolean = false,
    var isStore: Boolean = false,
    var isOwner: Boolean = false,

    var emailError: Boolean = false,
    var passwordError: Boolean = false,
    var password2Error: Boolean = false,
    var usernameError: Boolean = false,
    var ageError: Boolean = false,
    var addressError: Boolean = false,
)