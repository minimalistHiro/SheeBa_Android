package com.hiroki.sheeba.model

import com.google.firebase.Timestamp

data class NotificationModel (
    var uid: String = "",
    var title: String = "",
    var text: String = "",
    var username: String = "",
    var profileImageUrl: String = "",
    @field:JvmField
    var isRead: Boolean = false,
    var url: String = "",
    var imageUrl: String = "",
    var timestamp: Timestamp = Timestamp.now(),
)