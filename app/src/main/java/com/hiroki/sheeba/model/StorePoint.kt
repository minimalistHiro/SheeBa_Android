package com.hiroki.sheeba.model

data class StorePoint(
    var uid: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var getPoint: String = "",
    var username: String = "",
    @field:JvmField
    var isEvent: Boolean = false,
    var date: String = "",
)
