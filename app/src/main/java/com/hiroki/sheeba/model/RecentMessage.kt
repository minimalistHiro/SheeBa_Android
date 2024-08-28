package com.hiroki.sheeba.model

import java.util.Date

data class RecentMessage (
    var email: String = "",
    var fromId: String = "",
    var toId: String = "",
    var text: String = "",
    var profileImageUrl: String = "",
    @field:JvmField
    var isSendPay: Boolean = false,
    var username: String = "",
    var timestamp: Date,
)