package com.hiroki.sheeba.model

data class Friends (
    var uid: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var money: String = "",
    var username: String = "",
    @field:JvmField
    var isApproval: Boolean = false,        // お互いに友達承認済みか否か
    var approveUid: String = "",            // 友達承認申請者
)