package com.hiroki.sheeba.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.hiroki.sheeba.QrCodeAnalyzer
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.data.LoginUIState
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.data.SignUpUIState
import com.hiroki.sheeba.model.AlertNotification
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.model.Friends
import com.hiroki.sheeba.model.NotificationModel
import com.hiroki.sheeba.model.RecentMessage
import com.hiroki.sheeba.model.StorePoint
import com.hiroki.sheeba.screens.mapScreens.NavStoreDetailScreen
import com.hiroki.sheeba.screens.mapScreens.PinItem
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.Setting
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.Executors


open class ViewModel: ViewModel() {
    private val TAG = ViewModel::class.simpleName
    var signUpUIState = mutableStateOf(SignUpUIState(imageUri = null))
    var signUpUsernameScreenValidationPassed = mutableStateOf(false)
    var signUpAllValidationPassed = mutableStateOf(false)
    var signUpUsernamePassed = mutableStateOf(false)
    var loginUIState = mutableStateOf(LoginUIState())
    var loginEmailPassed = mutableStateOf(false)
    var loginAllValidationPassed = mutableStateOf(false)
    open var progress = mutableStateOf(false)
    var isHandleLoginProcess = mutableStateOf(false)                        // ログイン済みか否か
//    var isContainNotReadNotification = mutableStateOf(false)                // 未読のお知らせの有無

    // DB
//    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()
    var currentUser: MutableState<ChatUser?> = mutableStateOf(null)             // 現在のユーザー情報
    var chatUser: MutableState<ChatUser?> = mutableStateOf(null)                // 特定のユーザー情報
    var isStoreOwner: MutableState<Boolean> = mutableStateOf(false)             // 店舗オーナーか否か
    var allUsers = mutableListOf<ChatUser>()                                        // 全ユーザー情報（自分を除く）
    var allUsersContainSelf = mutableListOf<ChatUser>()                             // 全ユーザー情報（自分を含める）
    var storeUser: ChatUser? = null                                                 // 選択された店舗情報
    var storeUsers = mutableListOf<ChatUser?>()                                     // 全店舗ユーザー情報
    var rankMoneyUsers = mutableListOf<ChatUser?>()                                 // ポイント数上位%位までのユーザー情報
    var recentMessages =  mutableListOf<RecentMessage?>()                            // 全最新メッセージ
//    private val chatMessages = MutableStateFlow(listOf<ChatMessage?>())             // 全メッセージ
    var storePoints = mutableListOf<StorePoint?>()                                  // 全店舗ポイント情報
    var storePoint: MutableState<StorePoint?> = mutableStateOf(null)            // 特定の店舗ポイント情報
    var friends = mutableListOf<Friends?>()                                         // 全友達ユーザー情報
//    var resultFriends = mutableListOf<Friends?>()                                   // 検索結果に一致したユーザー情報
    var alertNotification: AlertNotification? = null                                // 速報
    var notification: NotificationModel? = null                                     // 特定のお知らせ
    var notifications = mutableListOf<NotificationModel?>()                         // 全お知らせ
    var pinItems = mutableListOf<PinItem?>()                                        // 全ピン情報

    // ダイアログ
    var isShowDialog = mutableStateOf(false)                    // ダイアログの表示有無
    var dialogTitle = mutableStateOf("")                        // ダイアログタイトル
    var dialogText = mutableStateOf("")                         // ダイアログメッセージ
    var isShowDialogForLogout = mutableStateOf(false)           // ログアウトへと誘導するダイアログの表示有無
    var isShowCompulsionLogoutDialog = mutableStateOf(false)    // 強制ログアウトダイアログの表示有無
    var isShowSuccessUpdateDialog = mutableStateOf(false)       // 更新完了ダイアログの表示有無

    // キーボード関連
    var sendPayText = mutableStateOf("0")                       // 送金テキスト
    var isTappedAC = mutableStateOf(false)                      // ACボタンがタップされたか否か
//    var getPoint = mutableStateOf(Setting.getPointFromStore)        // 獲得ポイント

    // 画面遷移
    var navStoreDetailScreen: NavStoreDetailScreen = NavStoreDetailScreen.TodaysGetPointScreen

    // QRコード関連
    // ImageAnalyzer.Analyzerを継承したQrCodeAnalyzerを内包したUseCaseを作成
    val qrCodeAnalyzeUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setTargetResolution(Setting.IMAGE_SIZE)
        .build()
        .also {
            it.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QrCodeAnalyzer { qrCode ->
                    _qrCode.value = qrCode
                    val chatUserUid = qrCode.rawValue.toString()
                    // QRコード読み取り処理
                    if(!isShowHandleScan.value) {
                        handleScan(chatUserUid = chatUserUid)
                        isShowHandleScan.value = true
                    }
                },
            )
        }
    private val _qrCode = mutableStateOf<Barcode?>(null)
    val qrCode: androidx.compose.runtime.State<Barcode?> = _qrCode
    var isQrCodeScanError = mutableStateOf(false)               // QRコード読み取りエラー
    var isSameStoreScanError = mutableStateOf(false)            // 同日同店舗スキャンエラー
    val delayMillis = 300L                                              // 押下後一時的に押下処理を無効化する時間(ms)
    var pushedAt = 0L                                                   // 前回押下時間(タイムスタンプ, ms)
    var isShowHandleScan = mutableStateOf(false)                // スキャン処理を一度したか否か

    // その他
    var isSendPay = mutableStateOf(false)                       // 送金処理か否か
    
    /**
     * 初期化処理
     *
     * @param event イベント
     * @return なし
     */
    fun init() {
        signUpUIState.value = SignUpUIState(imageUri = null)
        loginUIState.value = LoginUIState()
//        signUpUsernameScreenValidationPassed.value = false
//        signUpAllValidationPassed.value = false
//        signUpUsernamePassed.value = false
//        loginEmailPassed.value = false
//        loginAllValidationPassed.value = false
    }

    /**
     * 新規作成イベント。各イベントごとに処理を分ける。
     *
     * @param event イベント
     * @return なし
     */
    fun onSignUpEvent(event: SignUpUIEvent) {
//        validateSignUpDataWithRules()
        when(event) {
            is SignUpUIEvent.EmailChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    email = event.email
                )
            }

            is SignUpUIEvent.PasswordChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    password = event.password
                )
            }

            is SignUpUIEvent.Password2Change -> {
                signUpUIState.value = signUpUIState.value.copy(
                    password2 = event.password2
                )
            }

            is SignUpUIEvent.ProfileImageUrlChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    imageUri = event.imageUri
                )
            }

            is SignUpUIEvent.UsernameChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    username = event.username
                )
            }

            is SignUpUIEvent.AgeChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    age = event.age
                )
            }

            is SignUpUIEvent.AddressChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    address = event.address
                )
            }

            is SignUpUIEvent.SignUpButtonClicked -> {
                createNewAccount(
                    email = signUpUIState.value.email,
                    password = signUpUIState.value.password,
                    password2 = signUpUIState.value.password2,
                    imageUri = signUpUIState.value.imageUri,
                    username = signUpUIState.value.username,
                    age = signUpUIState.value.age,
                    address = signUpUIState.value.address,
                )
            }
        }
    }

    /**
     * ログインイベント。各イベントごとに処理を分ける。
     *
     * @param event イベント
     * @return なし
     */
    fun onLoginEvent(event: LoginUIEvent) {
//        validateLoginDataWithRules()
        when(event) {
            is LoginUIEvent.EmailChange -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
            }
            is LoginUIEvent.PasswordChange -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
            }
            is LoginUIEvent.LoginButtonClicked -> {
                handleLogin()
            }
        }
    }

    /**
     * 新規作成の各イベントごとのエラー処理
     *
     * @return なし
     */
//    private fun validateSignUpDataWithRules() {
//        val emailResult = Validator.validateEmail(
//            email = signUpUIState.value.email
//        )
//
//        val passwordResult = Validator.validatePassword(
//            password = signUpUIState.value.password
//        )
//
//        val password2Result = Validator.validatePassword(
//            password = signUpUIState.value.password2
//        )
//
//        val usernameResult = Validator.validateUsername(
//            username = signUpUIState.value.username
//        )
//
//        val ageResult = Validator.validateAge(
//            age = signUpUIState.value.age
//        )
//
//        val addressResult = Validator.validateAddress(
//            address = signUpUIState.value.address
//        )
//
//        signUpUIState.value = signUpUIState.value.copy(
//            emailError = emailResult.status,
//            passwordError = passwordResult.status,
//            password2Error = password2Result.status,
//            usernameError =  usernameResult.status,
//            ageError =  ageResult.status,
//            addressError =  addressResult.status,
//        )
//
//        signUpUsernameScreenValidationPassed.value = usernameResult.status && ageResult.status && addressResult.status
//        signUpAllValidationPassed.value = emailResult.status && passwordResult.status && password2Result.status
//        signUpUsernamePassed.value = usernameResult.status
//    }

    /**
     * ログインの各イベントごとのエラー処理
     *
     * @return なし
     */
//    private fun validateLoginDataWithRules() {
//        val emailResult = Validator.validateEmail(
//            email = loginUIState.value.email
//        )
//
//        val passwordResult = Validator.validatePassword(
//            password = loginUIState.value.password
//        )
//
//        loginUIState.value = loginUIState.value.copy(
//            emailError = emailResult.status,
//            passwordError = passwordResult.status,
//        )
//
//        loginAllValidationPassed.value = emailResult.status && passwordResult.status
//        loginEmailPassed.value = emailResult.status
//    }

    /**
     * 現在ユーザー情報を取得
     *
     * @return なし
     */
    fun fetchCurrentUser() {
        progress.value = true

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleError(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.toObject(ChatUser::class.java)?.let {
                        currentUser = mutableStateOf(it)
                    }

                    val currentUser = currentUser.value?.let {
                        // 初回特典アラート表示
                        if (!it.isFirstLogin && !it.isStore) {
                            handleAlert(
                                title = "",
                                text = "初回登録特典として\n${Setting.newRegistrationBenefits}ptプレゼント！"
                            )
                            val data = hashMapOf<String, Any>(
                                FirebaseConstants.isFirstLogin to true,
                            )
                            updateUser(document = uid, data = data)
                        }
                    }
                } else {
                    handleError(title = "", text = Setting.failureFetchUser, exception = null)
                }
                progress.value = false
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureFetchUser, exception = it)
            }
    }

    /**
     * UIDに一致するユーザー情報を取得
     *
     * @param uid UID
     * @return なし
     */
    fun fetchUser(uid: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.toObject(ChatUser::class.java)?.let {
                        chatUser = mutableStateOf(it)
                    }
                } else {
                    handleError(title = "", text = Setting.failureFetchUser, exception = null)
                }
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureFetchUser, exception = it)
            }
    }

    /**
     * 自分以外の全ユーザーを取得
     *
     * @return なし
     */
    fun fetchAllUserOtherThanSelf() {
        allUsers.clear()

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(ChatUser::class.java).let {
                        if(!it.isStore && currentUser.value?.uid != it.uid) {
                            allUsers.add(it)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchUser, exception = exception)
            }
    }

    /**
     * 自分を含めた全ユーザーを取得
     *
     * @return なし
     */
    fun fetchAllUsersContainSelf() {
        allUsersContainSelf.clear()

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(ChatUser::class.java).let {
                        // 追加するユーザーが店舗ユーザーでない場合のみ、追加する。
                        if(!it.isStore) {
                            allUsersContainSelf.add(it)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchUser, exception = exception)
            }
    }

    /**
     * 全ユーザーをポイントが高い順に並べて取得
     *
     * @return なし
     */
    fun fetchAllUsersOrderByMoney() {
        progress.value = true
        allUsers.clear()
        rankMoneyUsers.clear()

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(ChatUser::class.java)?.let {
                        if(!it.isStore || !it.money.isNullOrEmpty()) {
                            allUsers.add(it)
                        }
                    }
                }

                // ポイントが高い順に並び替える。
                val sortUsers = allUsers.sortedByDescending {
                    it?.money?.toInt()
                }

                var previousMoney = -1          // 一つ上位のポイント数
                var count = 0                   // 順位

                for(user in sortUsers) {
                    user?.let { user ->
                        user.money.toInt().let { money ->
                            // オーナーアカウント以外
                            if(!user.isOwner) {
                                // 指定順位以内であれば、ランキングに加える
                                if(count < Setting.rankingCount) {
                                    // ポイント数に変更があったら、順位を一つ変えるためカウント数を一つ加える。
                                    if(money != previousMoney) {
                                        count += 1
                                    }
                                    // データを上位5位までのユーザーに追加する。
                                    val data = ChatUser(
                                        username = user.username,
                                        profileImageUrl = user.profileImageUrl,
                                        money = user.money,
                                        ranking = "${count}")
                                    rankMoneyUsers.add(data)

                                    previousMoney = money
                                }
                            }
                        }
                    }
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchUser, exception = exception)
            }
    }

    /**
     * UIDに一致する店舗ポイント情報を取得
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @return なし
     */
    fun fetchStorePoint(document1: String, document2: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.storePoints)
            .document(document1)
            .collection(FirebaseConstants.user)
            .document(document2)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.toObject(StorePoint::class.java)?.let {
                        storePoint = mutableStateOf(it)
                    }
                } else {
                    handleError(title = "", text = Setting.failureFetchStorePoint, exception = null)
                }
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureFetchStorePoint, exception = it)
            }
    }

    /**
     * 店舗ポイント情報を取得
     *
     * @return なし
     */
    fun fetchStorePoints() {
        progress.value = true
        storePoints.clear()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            return
        }

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.storePoints)
            .document(uid)
            .collection(FirebaseConstants.user)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(StorePoint::class.java)?.let {
                        storePoints.add(it)
                    }
                }
                fetchAllStoreUsers()
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchStorePoint, exception = exception)
            }
    }

    /**
     * 全店舗ユーザーを取得
     *
     * @return なし
     */
    fun fetchAllStoreUsers() {
        progress.value = true
        storeUsers.clear()
        var storeUsersContainNotEnableScan = mutableListOf<ChatUser?>()                     // スキャン不可能を含めた店舗ユーザー

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(ChatUser::class.java)?.let {
                        if(it.isStore && it.isEnableScan) {
                            storeUsers.add(it)
                        }
                    }
                }
                // スキャン可能ユーザーのみ加える
//                for(user in storeUsersContainNotEnableScan) {
//                    if (user != null) {
//                        if(!user.isEnableScan) {
//                            storeUsers.add(user)
//                        }
//                    }
//                }

                // 番号順に並び変える。
                storeUsers.sortBy {
                    it?.no
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchUser, exception = exception)
            }
    }

    /**
     * 全店舗ユーザーを取得(Map用)
     *
     * @param なし
     * @return なし
     */
    fun fetchAllStoreUsersForMap() {
        progress.value = true
        storeUsers.clear()

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(ChatUser::class.java)?.let {
                        if(it.isStore && it.isEnableScan) {
                            storeUsers.add(it)
                        }
                    }
                }
                // ピン情報を取得する。
                fetchPinItems()
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchUser, exception = exception)
            }
    }

    /**
     * 全メッセージを取得
     *
     * @param toId トーク相手UID
     * @return なし
     */
//    fun fetchMessages(toId: String) {
//        val chatMessages = mutableListOf<ChatMessage>()
//
//        val fromId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
//            handleError(title = "", text = Setting.failureFetchUID, exception = null)
//            return
//        }
//
//        FirebaseFirestore
//            .getInstance()
//            .collection(FirebaseConstants.messages)
//            .document(fromId)
//            .collection(toId)
//            .orderBy(FirebaseConstants.timestamp, Query.Direction.ASCENDING)
//            .get()
//            .addOnSuccessListener { documents ->
//                for(document in documents) {
//                    document.toObject(ChatMessage::class.java).let {
//                        chatMessages.add(it)
//                    }
//                }
//                _uiCMState.update {
//                    chatMessages
//                }
//            }
//            .addOnFailureListener { exception ->
//                handleError(title = "", text = Setting.failureFetchMessages, exception = exception)
//            }
//    }

    /**
     * 全最新メッセージを取得
     *
     * @param なし
     * @return なし
     */
    fun fetchRecentMessages() {
        progress.value = true
        recentMessages.clear()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            return
        }

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.recentMessages)
            .document(uid)
            .collection(FirebaseConstants.message)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(RecentMessage::class.java).let {
                        recentMessages.add(it)
                    }
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchRecentMessage, exception = exception)
            }
    }

    /**
     * 全友達ユーザー情報を取得
     *
     * @param なし
     * @return なし
     */
    fun fetchFriends() {
        progress.value = true
        friends.clear()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            return
        }

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.friends)
            .document(uid)
            .collection(FirebaseConstants.user)
            .orderBy(FirebaseConstants.username, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(Friends::class.java).let {
                        friends.add(it)
                    }
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchFriends, exception = exception)
            }
    }

    /**
     * 検索に一致したユーザーを取得
     *
     * @param searchText 検索文字列
     * @return なし
     */
//    fun fetchSearchNames(searchText: String): List<ChatUser> {
//        val resultUsers = mutableListOf<ChatUser>()               // 検索結果に一致したユーザー情報
//
//        if (searchText.isEmpty()) {
//            return resultUsers
//        } else if (recentMessages.isEmpty()) {
//            // 最新メッセージが空の場合
//            resultUsers.addAll(
//                allUsers.filter { user ->
//                    // 全ユーザーの中で、既に友達申請しているユーザーを排除する
//                    friends.none { friend -> friend?.uid == user.uid } &&
//                            // 検索ワードに一致するユーザー名のみ含める
//                            user.username.contains(searchText, ignoreCase = true)
//                }
//            )
//        } else {
//            // 検索ワードに一致するユーザーをフィルタ
//            val filteredUsers = allUsers.filter {
//                it.username.contains(searchText, ignoreCase = true)
//            }
//
//            // 最近のメッセージのトーク相手のUIDを取得
//            val recentMessageIds = recentMessages.map { message ->
//                if (currentUser.value?.uid == message?.fromId) message?.toId else message?.fromId
//            }
//
//            // 最近メッセージをやり取りした相手を省く
//            resultUsers.addAll(
//                filteredUsers.filter { user ->
//                    !recentMessageIds.contains(user.uid)
//                }
//            )
//        }
//
//        return resultUsers
//    }

    /**
     * 速報を取得
     *
     * @return なし
     */
    fun fetchAlerts() {
        progress.value = true

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.alerts)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(AlertNotification::class.java)?.let {
                        alertNotification = it
                    }
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchNotification, exception = exception)
            }
    }

    /**
     * 全お知らせを取得
     *
     * @return なし
     */
    fun fetchNotifications() {
        progress.value = true
        notifications.clear()
//        isContainNotReadNotification.value = false

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            return
        }

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.notifications)
            .document(uid)
            .collection(FirebaseConstants.notification)
            .orderBy(FirebaseConstants.timestamp, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.toObject(NotificationModel::class.java).let {
                        notifications.add(it)
                        // 未読があった場合、お知らせに赤いバッジをつける
//                        if(!it.isRead) {
//                            isContainNotReadNotification.value = true
//                        }
                    }
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchNotification, exception = exception)
            }
    }

    /**
     * マップのピンを取得
     *
     * @param なし
     * @return なし
     */
    fun fetchPinItems() {
        pinItems.clear()

        for (store in storeUsers) {
            store?.let {
                if (it.pointX.isNotEmpty() && it.pointY.isNotEmpty()) {
                    val pinItem = PinItem(
                        uid = it.uid,
                        coordinate = LatLng(
                            it.pointY.toDouble(),
                            it.pointX.toDouble()
                        ),
                        buttonSize = 70f,
                        imageUrl = it.profileImageUrl,
                        storeName = it.username,
                    )
                    pinItems.add(pinItem)
                } else {
                    null
                }
            }
        }
    }

    /**
     * 新規作成
     *
     * @param email メールアドレス
     * @param password パスワード
     * @param password2 パスワード（確認用）
     * @param username ユーザー名
     * @param age 年代
     * @param address 住所
     * @return なし
     */
    private fun createNewAccount(
        email: String,
        password: String,
        password2: String,
        imageUri: Uri?,
        username: String,
        age: String,
        address: String
    ) {
        progress.value = true

        // 2つのパスワードが一致しない場合、エラーを出す。
        if(password != password2) {
            handleError(title = "", text = Setting.mismatchPassword, exception = null)
            return
        }

        // パスワードが6文字以下の場合、エラーを出す。
        if(password.length < 6) {
            handleError(title = "", text = Setting.weakPassword, exception = null)
            return
        }

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    if(imageUri == null) {
                        persistUser(
                            email = email,
                            profileImageUrl = "",
                            username = username,
                            age = age,
                            address = address
                        )
                    } else {
                        persistImage(
                            email = email,
                            imageUri = imageUri,
                            username = username,
                            age = age,
                            address = address
                        )
                    }
                }
                progress.value = false
                handleEmailVerification()
            }
            .addOnFailureListener {
                handleError(
                    title = Setting.failureCreateAccount,
                    text = "以下の可能性があります。\n・メールアドレスが既に使用されている\n・メールアドレスの形式が正しくない\n・ネットワークの接続が悪い",
                    exception = it
                )
                return@addOnFailureListener
            }
    }

    /**
     * メール送信処理
     *
     * @return なし
     */
    fun handleEmailVerification() {
        progress.value = true

        val user = FirebaseAuth.getInstance().currentUser ?: run {
            handleError(title = "", text = Setting.failureFetchUser, exception = null)
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener {
                PostOfficeAppRouter.navigateTo(Screen.ConfirmEmailScreen)
                progress.value = false
                return@addOnCompleteListener
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureSendEmail, exception = it)
                progress.value = false
                return@addOnFailureListener
            }
    }

    /**
     * ログイン
     *
     * @return なし
     */
    private fun handleLogin() {
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        progress.value = true

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    isHandleLoginProcess.value = true

                    val user = it.result.user
                    user?.let {
                        if(it.isEmailVerified) {
                            PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                            progress.value = false
                        } else {
                            PostOfficeAppRouter.navigateTo(Screen.NotConfirmEmailScreen)
                            progress.value = false
                        }
                    }
                }
            }
            .addOnFailureListener {
                handleError(
                    title = Setting.failureLogin,
                    text = "以下の可能性があります。\n・メールアドレスまたはパスワードが間違えている\n・ネットワークの接続が悪い",
                    exception = it
                )
            }
    }

    /**
     * ログイン（メールアドレス認証含む）
     *
     * @return なし
     */
    fun handleLoginWithConfirmEmail() {
        // サインアップの場合とログインの場合で代入する値を分ける
        val email = if(signUpUIState.value.email.isEmpty()) loginUIState.value.email else signUpUIState.value.email
        val password = if(signUpUIState.value.password.isEmpty()) loginUIState.value.password else signUpUIState.value.password

        progress.value = true

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val user = it.result.user
                user?.let {
                    if(!it.isEmailVerified) {
                        handleError(title = "", text = Setting.notConfirmEmail, exception = null)
                        return@let
                    }

                    // メールアドレス認証済み処理
//                    val data = hashMapOf<String, Any>(
//                        FirebaseConstants.isConfirmEmail to true,
//                    )
//                    updateUser(document = it.uid, data = data)
                    PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                }

                if(user == null) {
                    handleError(title = "", text = Setting.failureFetchUser, exception = null)
                    return@addOnCompleteListener
                }

                progress.value = false
            }
            .addOnFailureListener {
                handleError(
                    title = Setting.failureLogin,
                    text = "以下の可能性があります。\n・メールアドレスまたはパスワードが間違えている\n・ネットワークの接続が悪い",
                    exception = it
                )
            }
    }

    /**
     * ログイン（メール送信含む）
     *
     * @return なし
     */
    fun handleLoginWithEmailVerification() {
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        progress.value = true

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progress.value = false

                val user = it.result.user
                user?.let {
                    if(!it.isEmailVerified) {
                        it.sendEmailVerification()
                            .addOnSuccessListener {
                                PostOfficeAppRouter.navigateTo(Screen.ConfirmEmailScreen)
                            }
                            .addOnFailureListener {
                                handleError(title = "", text = Setting.failureSendEmail, exception = it)
                            }
                    } else {
                        PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                    }
                }

                if(user == null) {
                    handleError(title = "", text = Setting.failureFetchUser, exception = null)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                handleError(
                    title = Setting.failureLogin,
                    text = "以下の可能性があります。\n・メールアドレスまたはパスワードが間違えている\n・ネットワークの接続が悪い",
                    exception = it
                )
            }
    }

    /**
     * ログイン（メール送信含む）
     *
     * @return なし
     */
    fun handleSend(toId: String, chatText: String, lastText: String, isSendPay: Boolean) {
        progress.value = true

        val fromId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            return
        }

        val messageData = hashMapOf<String, Any>(
            FirebaseConstants.fromId to fromId,
            FirebaseConstants.toId to toId,
            FirebaseConstants.text to if (isSendPay) lastText else chatText,
            FirebaseConstants.isSendPay to isSendPay,
            FirebaseConstants.timestamp to Timestamp.now(),
        )

        // 自身のメッセージデータを保存
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.messages)
            .document(fromId)
            .collection(toId)
            .document()
            .set(messageData)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistMessage, exception = it)
            }

        // トーク相手のメッセージデータを保存
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.messages)
            .document(toId)
            .collection(fromId)
            .document()
            .set(messageData)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistMessage, exception = it)
            }

        // 自身の最新メッセージを保存
        chatUser.value?.let {
            persistRecentMessage(
                user = it,
                isSelf = true,
                fromId = fromId,
                toId = toId,
                text = if (isSendPay) lastText else chatText,
                isSendPay = isSendPay)
        }

        // トーク相手の最新メッセージを保存
        currentUser.value?.let {
            persistRecentMessage(
                user = it,
                isSelf = false,
                fromId = fromId,
                toId = toId,
                text = if (isSendPay) lastText else chatText,
                isSendPay = isSendPay)
        }

        progress.value = false
    }

    /**
     * 入力したメールアドレスにパスワード再設定リンクを送る
     *
     * @return なし
     */
    fun handleSendResetPasswordLink() {
        val email = loginUIState.value.email
        progress.value = true

        FirebaseAuth
            .getInstance()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                handleAlert(title = "", text = "入力したメールアドレスにパスワード再設定用のURLを送信しました。")
                progress.value = false
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureSendEmail, exception = null)
                return@addOnFailureListener
            }
    }

    /**
     * 承認処理
     *
     * @param toId 承認相手UID
     * @return なし
     */
    fun handleApprove(toId: String) {

    }

    /**
     * ログアウト
     *
     * @return なし
     */
    fun handleLogout() {
        FirebaseAuth.getInstance().signOut()

        val authStateListener = AuthStateListener {
            if(it.currentUser == null) {
                // バックグラウンド処理
                viewModelScope.launch(Dispatchers.IO) {
                    PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
                }
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    /**
     * ログアウトのみ
     *
     * @return なし
     */
    fun handleLogoutOnly() {
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * 退会処理
     *
     * @return なし
     */
    fun handleWithdrawal() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }
        // ユーザー情報削除
        deleteUser(uid)

        // メッセージを削除
        for (recentMessage in recentMessages) {
            recentMessage?.let {
                deleteMessage(document = uid, collection = if (uid == it.fromId) it.toId else it.fromId)
            }
        }

        // 最新メッセージを削除
        for (recentMessage in recentMessages) {
            recentMessage?.let {
                deleteRecentMessage(document1 = uid, document2 = if (uid == it.fromId) it.toId else it.fromId)
            }
        }

        // 友達を削除
        for (friend in friends) {
            friend?.let {
                deleteFriend(document1 = uid, document2 = it.uid)
            }
        }

        // 店舗ポイント情報を削除
        for (storePoint in storePoints) {
            storePoint?.let {
                deleteStorePoint(document1 = uid, document2 = it.uid)
            }
        }

        // お知らせを削除
        for (notification in notifications) {
            notification?.let {
                deleteNotification(document1 = uid, document2 = it.title)
            }
        }

        // 画像の削除
        deleteImage(uid)
        // 認証情報削除
        deleteAuth()
    }

    /**
     * アラート処理
     *
     * @param title エラータイトル
     * @param text エラーメッセージ
     * @param exception エラー内容
     * @return なし
     */
    fun handleAlert(title: String, text: String) {
        progress.value = false
        isShowDialog.value = true
        dialogTitle.value = title
        dialogText.value = text
        Log.d(TAG, text)
    }

    /**
     * エラー処理
     *
     * @param title エラータイトル
     * @param text エラーメッセージ
     * @param exception エラー内容
     * @return なし
     */
    fun handleError(title: String, text: String, exception: Exception?) {
        progress.value = false
        isShowDialog.value = true
        dialogTitle.value = title
        dialogText.value = text
        if (exception == null) {
            Log.d(TAG, text)
        } else {
            Log.d(TAG, "${exception.localizedMessage}")
        }
    }

    /**
     * ログアウトへと誘導するエラー処理
     *
     * @param title エラータイトル
     * @param text エラーメッセージ
     * @param exception エラー内容
     * @return なし
     */
    fun handleErrorForLogout(title: String, text: String, exception: Exception?) {
        progress.value = false
        isShowDialogForLogout.value = true
        dialogTitle.value = title
        dialogText.value = text
        if (exception == null) {
            Log.d(TAG, text)
        } else {
            Log.d(TAG, "${exception.localizedMessage}")
        }
    }

    /**
     * ユーザー情報を保存
     *
     * @param email メールアドレス
     * @param username ユーザー名
     * @param age 年代
     * @param address 住所
     * @return なし
     */
    fun persistUser(
        email: String,
        profileImageUrl: String,
        username: String,
        age: String,
        address: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        // ユーザー情報を格納
        val data = hashMapOf<String, Any>(
            FirebaseConstants.uid to uid,
            FirebaseConstants.email to email,
            FirebaseConstants.profileImageUrl to profileImageUrl,
            FirebaseConstants.money to Setting.newRegistrationBenefits,
            FirebaseConstants.username to username,
            FirebaseConstants.age to age,
            FirebaseConstants.address to address,
//            FirebaseConstants.isConfirmEmail to false,
            FirebaseConstants.isFirstLogin to false,
            FirebaseConstants.isStore to false,
            FirebaseConstants.isOwner to false,
            FirebaseConstants.isStoreOwner to isStoreOwner.value,
            FirebaseConstants.os to "Android"
        )

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(uid)
            .set(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistUser, exception = it)
            }
    }

    /**
     * 画像を保存
     *
     * @param email メールアドレス
     * @param imageUri 画像Uri
     * @param username ユーザー名
     * @param age 年代
     * @param address 住所
     * @return なし
     */
    fun persistImage(
        email: String,
        imageUri: Uri?,
        username: String,
        age: String,
        address: String,
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }
        val imageUri = imageUri ?: run {
            handleError(title = "", text = Setting.failurePersistImage, exception = null)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child(uid)

        storageRef
            .putFile(imageUri)
            .continueWithTask { task ->
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        persistUser(email = email, profileImageUrl = it.toString(), username = username, age = age, address = address)
                    }
                    .addOnFailureListener {
                        persistUser(email = email, profileImageUrl = "", username = username, age = age, address = address)
                        handleError(title = "", text = Setting.failurePersistImage, exception = it)
                    }
            }
            .addOnFailureListener {
                persistUser(email = email, profileImageUrl = "", username = username, age = age, address = address)
                handleError(title = "", text = Setting.failurePersistImage, exception = it)
            }
    }

    /**
     * 最新メッセージを保存
     *
     * @param user ユーザー情報
     * @param isSelf 自身のデータか否か
     * @param fromId 送信者UID
     * @param toId 受信者UID
     * @param text テキスト
     * @param isSendPay 送金の有無
     * @return なし
     */
    fun persistRecentMessage(
        user: ChatUser,
        isSelf: Boolean,
        fromId: String,
        toId: String,
        text: String,
        isSendPay: Boolean
    ) {
        // ユーザー情報を格納
        val data = hashMapOf<String, Any>(
            FirebaseConstants.email to user.email,
            FirebaseConstants.text to text,
            FirebaseConstants.fromId to fromId,
            FirebaseConstants.toId to toId,
            FirebaseConstants.profileImageUrl to user.profileImageUrl,
            FirebaseConstants.isSendPay to isSendPay,
            FirebaseConstants.username to user.username,
            FirebaseConstants.timestamp to Timestamp.now(),
        )

        var document: DocumentReference

        if (isSelf) {
            document = FirebaseFirestore
                .getInstance()
                .collection(FirebaseConstants.recentMessages)
                .document(fromId)
                .collection(FirebaseConstants.message)
                .document(toId)
        } else {
            document = FirebaseFirestore
                .getInstance()
                .collection(FirebaseConstants.recentMessages)
                .document(toId)
                .collection(FirebaseConstants.message)
                .document(fromId)
        }

        document.set(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistRecentMessage, exception = it)
            }
    }

    /**
     * 友達情報を保存
     *
     * @param chatUser 登録先ユーザー情報
     * @return なし
     */
    fun persistFriend(
        chatUser: ChatUser,
    ) {
        progress.value = true

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        // ユーザー情報を格納
        currentUser.value?.let {
            // 自身の友達データを保存
            val myData = hashMapOf<String, Any>(
                FirebaseConstants.uid to chatUser.uid,
                FirebaseConstants.email to chatUser.email,
                FirebaseConstants.profileImageUrl to chatUser.profileImageUrl,
                FirebaseConstants.money to chatUser.money,
                FirebaseConstants.username to chatUser.username,
                FirebaseConstants.isApproval to false,
                FirebaseConstants.approveUid to it.uid,
            )

            FirebaseFirestore
                .getInstance()
                .collection(FirebaseConstants.friends)
                .document(uid)
                .collection(FirebaseConstants.user)
                .document(chatUser.uid)
                .set(myData)
                .addOnFailureListener {
                    handleError(title = "", text = Setting.failurePersistFriends, exception = it)
                }

            // リクエスト相手の友達データを保存
            val chatUserData = hashMapOf<String, Any>(
                FirebaseConstants.uid to it.uid,
                FirebaseConstants.email to it.email,
                FirebaseConstants.profileImageUrl to it.profileImageUrl,
                FirebaseConstants.money to it.money,
                FirebaseConstants.username to it.username,
                FirebaseConstants.isApproval to false,
                FirebaseConstants.approveUid to it.uid,
            )

            FirebaseFirestore
                .getInstance()
                .collection(FirebaseConstants.friends)
                .document(chatUser.uid)
                .collection(FirebaseConstants.user)
                .document(uid)
                .set(chatUserData)
                .addOnFailureListener {
                    handleError(title = "", text = Setting.failurePersistFriends, exception = it)
                }
        }

        progress.value = false
    }


    /**
     * 店舗ポイント情報を保存
     *
     * @param document1 ドキュメント
     * @param document2 ドキュメント
     * @param data データ
     * @return なし
     */
    fun persistStorePoint(document1: String, document2: String, data: HashMap<String, Any>) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.storePoints)
            .document(document1)
            .collection(FirebaseConstants.user)
            .document(document2)
            .set(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistStorePoint, exception = it)
            }
    }

    /**
     * お知らせを保存
     *
     * @param document1 ドキュメント
     * @param document2 ドキュメント
     * @param data データ
     * @return なし
     */
    fun persistNotification(document1: String, document2: String, data: HashMap<String, Any>) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.notifications)
            .document(document1)
            .collection(FirebaseConstants.notification)
            .document(document2)
            .set(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistNotification, exception = it)
            }
    }

    /**
     * ユーザー情報を更新
     *
     * @param document ドキュメント
     * @param data データ
     * @return なし
     */
    fun updateUser(document: String, data: HashMap<String, Any>) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(document)
            .update(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureUpdateUser, exception = it)
            }
    }

    /**
     * 友達情報を更新
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @param data データ
     * @return なし
     */
    fun updateFriend(document1: String, document2: String, data: HashMap<String, Any>) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.friends)
            .document(document1)
            .collection(FirebaseConstants.user)
            .document(document2)
            .update(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureUpdateUser, exception = it)
            }
    }

    /**
     * お知らせを更新
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @param data データ
     * @return なし
     */
    fun updateNotification(document1: String, document2: String, data: HashMap<String, Any>) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.notifications)
            .document(document1)
            .collection(FirebaseConstants.notification)
            .document(document2)
            .update(data)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureUpdateNotification, exception = it)
            }
    }

    /**
     * 画像を更新
     *
     * @param uid UID
     * @param profileImageUrl 画像URL
     * @return なし
     */
    fun updateImage(uid: String, profileImageUrl: String) {
        // 自身のユーザー情報を更新
        val data = hashMapOf<String, Any>(
            FirebaseConstants.profileImageUrl to profileImageUrl,
        )
        updateUser(document = uid, data = data)
    }

    /**
     * ユーザー情報を削除
     *
     * @param document ドキュメント
     * @return なし
     */
    fun deleteUser(document: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(document)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteUser, exception = it)
            }
    }

    /**
     * 最新メッセージを削除
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @return なし
     */
    fun deleteRecentMessage(document1: String, document2: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.recentMessages)
            .document(document1)
            .collection(FirebaseConstants.message)
            .document(document2)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteRecentMessage, exception = it)
            }
    }

    /**
     * メッセージを削除
     *
     * @param document ドキュメント
     * @param collection コレクション
     * @return なし
     */
    fun deleteMessage(document: String, collection: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.messages)
            .document(document)
            .collection(collection)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteRecentMessage, exception = it)
            }
    }

    /**
     * 画像を削除
     *
     * @param path パス
     * @return なし
     */
    fun deleteImage(path: String) {
        FirebaseStorage
            .getInstance()
            .reference
            .child(path)
            .delete()
            .addOnFailureListener {
                Log.d(TAG, Setting.failureDeleteImage)
            }
    }

    /**
     * 友達情報を削除
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @return なし
     */
    fun deleteFriend(document1: String, document2: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.friends)
            .document(document1)
            .collection(FirebaseConstants.user)
            .document(document2)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteFriend, exception = it)
            }
    }

    /**
     * 店舗ポイント情報を削除
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @return なし
     */
    fun deleteStorePoint(document1: String, document2: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.storePoints)
            .document(document1)
            .collection(FirebaseConstants.user)
            .document(document2)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteStorePoint, exception = it)
            }
    }

    /**
     * お知らせを削除
     *
     * @param document1 ドキュメント1
     * @param document2 ドキュメント2
     * @return なし
     */
    fun deleteNotification(document1: String, document2: String) {
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.notifications)
            .document(document1)
            .collection(FirebaseConstants.notification)
            .document(document2)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteNotification, exception = it)
            }
    }

    /**
     * 認証情報削除
     *
     * @return なし
     */
    fun deleteAuth() {
        FirebaseAuth.getInstance().currentUser?.delete()
    }

    /**
     * キーボード入力から実行処理を分配する
     *
     * @param keyboard 入力されたキーボード
     * @return なし
     */
    fun applyKeyboard(keyboard: String) {
        // 入力したキーボードからテータスを振り分ける。
        if(keyboard == "AC") {
            sendPayText.value = "0"
            isTappedAC.value = true
        } else {
            tappedNumberPadProcess(keyboard)
        }
    }

    /**
     * 数字キーボード実行処理
     *
     * @param keyboard 入力されたキーボード
     * @return なし
     */
    fun tappedNumberPadProcess(keyboard: String) {
        // テキストが初期値"0"の時に、"0"若しくは"00"が入力された時、何もしない。
        if((sendPayText.value == "0") && (keyboard == "0" || keyboard == "00")) {
            return
        }

        // 連続で数字が入力された場合と、"AC"入力後に数字が入力された場合に分ける。
        if(!isTappedAC.value) {
            // テキストに表示できる最大数字を超えないように制御
            if(isCheckOverMaxNumberOfDigits((sendPayText.value + keyboard))) {
                return
            }

            if(sendPayText.value == "0") {
                sendPayText.value = keyboard
            } else {
                sendPayText.value += keyboard
            }
        } else {
            // AC押下後に、"00"が入力された時、"0"と表記する。
            if(keyboard == "00") {
                sendPayText.value = "0"
            } else {
                sendPayText.value = keyboard
            }
        }
        isTappedAC.value = false
    }

    /**
     * 計算結果がテキスト最大文字数を超えているかをチェックする
     *
     * @param numberText 入力されたキーボード
     * @return なし
     */
    fun isCheckOverMaxNumberOfDigits(numberText: String): Boolean {
        if(numberText.length > Setting.maxNumberOfDigits) {
            return true
        }
        return false
    }

    /**
     * 店舗ポイント情報が本日取得済みか否かを判断
     *
     * @param user ユーザー
     * @return 全店舗ユーザーの中に今日取得した店舗ポイント情報を確保していた場合True、そうでない場合false。
     */
    fun isGetStorePointToday(user: ChatUser): Boolean {
        for (storePoint in storePoints) {
            // 全店舗ユーザーの中に今日取得した店舗ポイント情報を確保していた場合True。
            if (storePoint != null) {
                if (user.uid == storePoint.uid && storePoint.date == dateFormat(LocalDate.now())) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 本日取得済みの店舗ポイント情報数をカウント
     *
     * @return 本日取得済みの店舗ポイント情報数
     */
    fun countGetStorePointToday(): Int {
        var count: Int = 0

        for (storePoint in storePoints) {
            if (storePoint != null) {
                if (storePoint.date == dateFormat(LocalDate.now())) {
                    count += 1
                }
            }
        }
        return count
    }

    /**
     * QRコード読み取り処理
     *
     * @param chatUserUid 読み取ったQRコードのUID
     * @return なし
     */
    private fun handleScan(chatUserUid: String) {
        // 同アカウントのQRコードを読み取ってしまった場合、エラーを発動。
        if(currentUser.value?.uid == chatUserUid) {
            isQrCodeScanError.value = true
            return
        }

        // URL、またはUIDの文字数（28文字）以外を読み取ったら、スキャンエラーを表示する
        if(chatUserUid.contains("http") || chatUserUid.length != 28) {
            isQrCodeScanError.value = true
            PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
            return
        }

        // ユーザーを取得
        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(chatUserUid)
            .get()
            .addOnSuccessListener { document ->
                Log.d(TAG, document.toString())
                if (document != null) {
                    document.toObject(ChatUser::class.java)?.let {
                        if(it.uid != "") {
                            chatUser = mutableStateOf(it)
                        }
                    }
                    // スキャンしたQRコードからUIDを取得できた場合、店舗ポイント情報を取得。
                    if(chatUser.value?.uid != "") {
                        currentUser.value?.let {
                            // 店舗ポイント情報を取得
                            FirebaseFirestore
                                .getInstance()
                                .collection(FirebaseConstants.storePoints)
                                .document(it.uid)
                                .collection(FirebaseConstants.user)
                                .document(chatUserUid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        document.toObject(StorePoint::class.java)?.let {
                                            storePoint = mutableStateOf(it)
                                        }
                                        divideScanProcess()
                                    } else {
                                        handleError(title = "", text = Setting.failureFetchStorePoint, exception = null)
                                    }
                                }
                                .addOnFailureListener {
                                    handleError(title = "", text = Setting.failureFetchStorePoint, exception = it)
                                }
                        }
                    } else {
                        // スキャンしたQRコードからUIDを取得できなかった場合、スキャンエラー画面を表示する。
                        isQrCodeScanError.value = true
                        PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                    }
                } else {
                    // documentを取得できなかった場合、スキャンエラー画面を表示する。
                    isQrCodeScanError.value = true
                    PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                }
            }
            .addOnFailureListener {
                // uidを取得できなかった場合、スキャンエラー画面を表示する。
                isQrCodeScanError.value = true
                PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
            }
    }

    /**
     * QRコード読み取り結果を場合分けする。
     *
     * @return なし
     */
    private fun divideScanProcess() {
        // 店舗ポイントアカウントの場合
        if(chatUser.value?.isStore == true) {
            // スキャン可能である場合。
            if(chatUser.value?.isEnableScan == true) {
                // 店舗ポイント情報がある場合。
                storePoint.value?.let {
                    // uidの取得がうまくいかない場合があるので、ここでも条件分岐をする。
                    if(it.uid == "" || it.uid == "D") {
                        handleGetPointFromStore()
                        PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                    } else {
                        // 店舗QRコードが同日に2度以上のスキャンでない場合
                        if(it.date != dateFormat(LocalDate.now())) {
                            handleGetPointFromStore()
                            PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                        } else {
                            isSameStoreScanError.value = true
                            PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                        }
                    }
                }
                // 店舗ポイント情報がない場合、ポイントを獲得する。
                if(storePoint.value == null) {
                    handleGetPointFromStore()
                    PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
                }
            } else {
                isQrCodeScanError.value = true
                PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
            }
        } else if(chatUser.value?.isStore == false) {
//            if(!isQrCodeScanError.value) {
                // ユーザーにポイントを送る画面に遷移
                PostOfficeAppRouter.navigateTo(Screen.SendPayScreen)
//            }
        } else {
            // スキャンエラー画面を表示する
            isQrCodeScanError.value = true
            PostOfficeAppRouter.navigateTo(Screen.GetPointScreen)
        }
    }

    /**
     * 店舗からポイント取得処理
     *
     * @return なし
     */
    private fun handleGetPointFromStore() {
        val intCurrentUserMoney = currentUser.value?.money?.toInt()
        // 残高に取得ポイントを足す
        var calculatedCurrentUserMoney = chatUser.value?.getPoint?.let { intCurrentUserMoney?.plus(it) }

        // 自身のユーザー情報を更新
        val userData = hashMapOf<String, Any>(
            FirebaseConstants.money to calculatedCurrentUserMoney.toString(),
        )
        currentUser.value?.let {
            updateUser(document = it.uid, data = userData)
        }

        // 店舗ポイント情報を更新
        val storePointData = hashMapOf<String, Any>(
            FirebaseConstants.uid to (chatUser.value?.uid ?: ""),
            FirebaseConstants.email to (chatUser.value?.email ?: ""),
            FirebaseConstants.profileImageUrl to (chatUser.value?.profileImageUrl ?: ""),
            FirebaseConstants.getPoint to (chatUser.value?.getPoint.toString() ?: ""),
            FirebaseConstants.username to (chatUser.value?.username ?: ""),
            FirebaseConstants.date to dateFormat(LocalDate.now())
        )

        chatUser.value?.let { chatUser ->
            currentUser.value?.let { currentUser ->
                persistStorePoint(
                    document1 = currentUser.uid,
                    document2 = chatUser.uid,
                    data = storePointData
                )
            }
        }
    }

    /**
     * 送ポイント処理
     *
     * @param navController
     * @param isQRScan QRコードスキャンによる送ポイントか否か
     * @return なし
     */
    fun handleSendPoint(navController: NavHostController, isQRScan: Boolean) {
        chatUser.value?.let {
            val chatUserMoney = it.money.toInt()
            val currentUserMoney = currentUser.value?.money?.toInt()
            val sendPayText = sendPayText.value.toInt()

            // TODO: -  互いに友達登録していない場合、新規友達登録をする。

            // 各ユーザーの残高を計算
            val calculatedChatUserMoney = chatUserMoney + sendPayText
            val calculatedCurrentUserMoney = currentUserMoney?.minus(sendPayText)

            // 各ユーザーの残高が0を下回る場合、アラートを発動
            if (calculatedCurrentUserMoney != null) {
                if((calculatedChatUserMoney < 0) || (calculatedCurrentUserMoney < 0)) {
                    handleError(title = "", text = "入力数値が残ポイントを超えています。", exception = null)
                    return
                }
            }

            // 送ポイント相手のデータを更新
            val chatUserData = hashMapOf<String, Any>(
                FirebaseConstants.money to calculatedChatUserMoney.toString(),
            )
            updateUser(document = it.uid, data = chatUserData)

            // 自身のデータを更新
            val userData = hashMapOf<String, Any>(
                FirebaseConstants.money to calculatedCurrentUserMoney.toString(),
            )
            currentUser.value?.let {
                updateUser(document = it.uid, data = userData)
            }

            navController.navigate(Setting.chatLogScreen)
        }
    }

    /**
     * トップ画像を更新
     *
     * @param imageUri 画像URI
     * @return なし
     */
    fun handleUpdateImage(imageUri: Uri?) {
        progress.value = true
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        // 画像削除
        deleteImage(path = uid.toString())

        // FIreStorageに保存
        val imageUri = imageUri ?: run {
            handleError(title = "", text = Setting.failurePersistImage, exception = null)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child(uid)

        storageRef
            .putFile(imageUri)
            .continueWithTask { task ->
                storageRef.downloadUrl
                    .addOnSuccessListener {
                        updateImage(uid = uid, profileImageUrl = it.toString())
                        progress.value = false
                        isShowSuccessUpdateDialog.value = true
                    }
                    .addOnFailureListener {
                        handleError(title = "", text = Setting.failureUpdateImage, exception = it)
                    }
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureUpdateImage, exception = it)
            }
    }

    /**
     * 取得したい日付を（yyyy年M月dd日）の形で取り出す
     *
     * @param date 日付
     * @return なし
     */
    fun dateFormat(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy年M月dd日")
        val formattedDateTime = date.format(formatter)
        return formattedDateTime
    }

    /**
     * Date型をStringに変換
     *
     * @param date 日付
     * @return なし
     */
    fun convertDateToString(date: Date): String {
        val formatter = SimpleDateFormat("yyyy年M月dd日 HH:mm")
        val formattedDateTime = formatter.format(date)
        return formattedDateTime
    }

    /**
     * QRコードを生成
     *
     * @param data QRコード化したいデータ
     * @return bitmap
     */
    fun createQrCode(data: String): Bitmap? {
        return try {
            val bitMatrix = createBitMatrix(data)
            bitMatrix?.let { createBitmap(it) }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * BitMatrixを作成する
     *
     * @param data QRコード化したいデータ
     * @return bitmap
     */
    fun createBitMatrix(data: String): BitMatrix? {
        val multiFormatWriter = MultiFormatWriter()
        val hints = mapOf(
            // マージン
            EncodeHintType.MARGIN to 0,
            // 誤り訂正レベルを一番低いレベルで設定 エンコード対象のデータ量が少ないため
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
        )

        return multiFormatWriter.encode(
            data, // QRコード化したいデータ
            com.google.zxing.BarcodeFormat.QR_CODE, // QRコードにしたい場合はこれを指定
            200, // 生成されるイメージの高さ(px)
            200, // 生成されるイメージの横幅(px)
            hints // オプション
        )
    }

    /**
     * BitMapを作成する
     *
     * @param bitMatrix bitMatrix
     * @return bitmap
     */
    fun createBitmap(bitMatrix: BitMatrix): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    }
}