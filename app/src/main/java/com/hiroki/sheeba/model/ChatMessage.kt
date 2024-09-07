package com.hiroki.sheeba.model

import com.google.firebase.Timestamp

data class ChatMessage (
    var fromId: String = "",
    var toId: String = "",
    var text: String = "",
    @field:JvmField
    var isSendPay: Boolean = false,
    var timestamp: Timestamp = Timestamp.now(),
)