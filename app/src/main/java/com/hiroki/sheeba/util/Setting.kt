package com.hiroki.sheeba.util

import android.util.Size

object Setting {
    // キャンペーン値
    val newRegistrationBenefits = "20"              // 新規登録特典。プレゼントポイント
    val getPointFromStore = "1"                     // 店舗からの取得ポイント

    // 各種設定
    val maxNumberOfDigits = 6                       // 最大送金桁数
    val IMAGE_SIZE = Size(1080, 1920)   // 画像の解析サイズ
    val privacyPolicyURL = "https://www.shibaginzadori.com/?page_id=1070" // プライバシーポリシーURL

    // Screens
    val entryScreen = "entryScreen"
    val setUpUsernameScreen = "setUpUsernameScreen"
    val setUpEmailScreen = "setUpEmailScreen"
    val homeScreen = "homeScreen"
    val sendPayScreen = "sendPayScreen"
    val cameraScreen = "cameraScreen"
    val getPointScreen = "getPointScreen"
    val accountScreen = "accountScreen"
    val updateUsernameScreen = "updateUsernameScreen"
    val updateImageScreen = "updateImageScreen"

    // TutorialMessage
    val tutorialLastPage = 4
    fun tutorialText(page: Int): String {
        when(page) {
            1 -> return "SheeBaを\nダウンロードしていただき\nありがとうございます"
            2 -> return "各店舗にあるQRコードを読み取って\nポイントを貯めることができます"
            3 -> return "貯まったポイントは\n景品と交換することができます"
            4 -> return "QRコードをたくさんスキャンして\n欲しい商品をゲットしよう！"
        }
        return ""
    }

    // ErrorCode
    val mismatchPassword = "パスワードとパスワード（確認用）が一致しません。"
    val failureCreateAccount = "アカウントの作成に失敗しました。"
    val failureLogin = "ログインに失敗しました。"
    val failureSendEmail = "メール送信に失敗しました。"
    val failureFetchUID = "UIDの取得に失敗しました。"
    val failureFetchUser = "ユーザー情報の取得に失敗しました。"
    val failureFetchStorePoint = "店舗ポイント情報の取得に失敗しました。"
    val failurePersistUser = "ユーザー情報の保存に失敗しました。"
    val failurePersistStorePoint = "店舗ポイント情報の保存に失敗しました。"
    val failurePersistImage = "画像の保存に失敗しました。"
    val failureUpdateUser = "ユーザー情報の更新に失敗しました。"
    val failureUpdateImage = "画像の更新に失敗しました。"
    val failureDeleteData = "データ削除に失敗しました。"
    val failureDeleteUser = "ユーザー情報の削除に失敗しました。"
    val failureDeleteMessage = "メッセージの削除に失敗しました。"
    val failureDeleteRecentMessage = "最新メッセージの削除に失敗しました。"
    val failureDeleteFriend = "友達情報の削除に失敗しました。"
    val failureDeleteStorePoint = "店舗ポイント情報の削除に失敗しました。"
    val failureDeleteImage = "画像の削除に失敗しました。"
    val failureDeleteAuth = "認証情報の削除に失敗しました。"
}