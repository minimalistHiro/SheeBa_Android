package com.hiroki.sheeba.model

data class ChatUser (
    var uid: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var money: String = "",
    var username: String = "",
    var age: String = "",
    var address: String = "",
    var isConfirmEmail: Boolean = false,
    var isFirstLogin: Boolean = false,
    var isStore: Boolean = false,
    var isOwner: Boolean = false,
)

object ChatUserItem {
    val ages = listOf(
        "〜19歳",
        "20代",
        "30代",
        "40代",
        "50代",
        "60歳〜",
        )
    val addresses = listOf(
        "川口市（'芝'が付く地域）",
        "川口市（'芝'が付かない地域）",
        "蕨市",
        "さいたま市",
        "その他",
    )
}

enum class ChatUserSelected() {
    Age,
    Address,
}
