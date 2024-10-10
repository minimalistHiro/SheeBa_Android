package com.hiroki.sheeba.model

import com.google.firebase.Timestamp

data class RecentMessage (
    var email: String = "",
    var fromId: String = "",
    var toId: String = "",
    var text: String = "",
    var profileImageUrl: String = "",
    @field:JvmField
    var isSendPay: Boolean = false,
    @field:JvmField
    var isRead: Boolean = false,
    var username: String = "",
    var timestamp: Timestamp = Timestamp.now(),
)