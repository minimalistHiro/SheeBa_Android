package com.hiroki.sheeba.model

data class ChatUser (
    var uid: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var money: String = "",
    var username: String = "",
    var age: String = "",
    var address: String = "",
//    @field:JvmField
//    var isConfirmEmail: Boolean = false,
    @field:JvmField
    var isFirstLogin: Boolean = false,
    @field:JvmField
    var isStore: Boolean = false,
    @field:JvmField
    var isOwner: Boolean = false,
    var os: String = "Android",
    var ranking: String = "",

    // 店舗ユーザー
    var no: Int = 0,
    @field:JvmField
    var isEnableScan: Boolean = false,
    var getPoint: Int = 0,
    var pointX: String = "",
    var pointY: String = "",
    var genre: String = "",
    var phoneNumber: String = "",
    var webURL: String = "",
    var movieURL: String = "",
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
