package com.hiroki.sheeba.model

data class Stores (
    var uid: String = "",
    var storename: String = "",
    var no: Int = 0,
    var genre: String = "",
    var phoneNumber: String = "",
    var webURL: String = "",
    var movieURL: String = "",
    var profileImageUrl: String = "",
    var getPoint: Int = 0,
    @field:JvmField
    var isEnableScan: Boolean = false,
    @field:JvmField
    var isEvent: Boolean = false,
    var pointX: String = "",
    var pointY: String = "",
)