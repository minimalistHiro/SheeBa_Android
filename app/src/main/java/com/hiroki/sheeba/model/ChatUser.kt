package com.hiroki.sheeba.model

data class ChatUser (
    val uid: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val money: String = "",
    val username: String = "",
    val age: String = "",
    val address: String = "",
    val isConfirmEmail: Boolean = false,
    val isFirstLogin: Boolean = false,
    val isStore: Boolean = false,
    val isOwner: Boolean = false,
)