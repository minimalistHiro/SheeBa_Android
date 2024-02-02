package com.hiroki.sheeba.viewModel

import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.barcode.common.Barcode
import com.hiroki.sheeba.QrCodeAnalyzer
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.data.LoginUIState
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.data.SignUpUIState
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.model.StorePoint
import com.hiroki.sheeba.util.FirebaseConstants
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.util.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

class ViewModel: ViewModel() {
    private val TAG = ViewModel::class.simpleName
    var signUpUIState = mutableStateOf(SignUpUIState(imageUri = null))
    var signUpUsernameScreenValidationPassed = mutableStateOf(false)
    var signUpAllValidationPassed = mutableStateOf(false)
    var signUpUsernamePassed = mutableStateOf(false)
    var loginUIState = mutableStateOf(LoginUIState())
    var loginEmailPassed = mutableStateOf(false)
    var loginAllValidationPassed = mutableStateOf(false)
    var progress = mutableStateOf(false)

    // DB
//    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()
    var currentUser = mutableStateOf(ChatUser())                                        // 現在のユーザー情報
    var chatUser: MutableState<ChatUser?> = mutableStateOf(null)                // 特定のユーザー情報
    var storePoints = mutableListOf<StorePoint>()                                       // 全店舗ポイント情報
    var storePoint: MutableState<StorePoint?> = mutableStateOf(null)            // 特定の店舗ポイント情報

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
    var getPoint = mutableStateOf(Setting.getPointFromStore)        // 獲得ポイント

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
    
    /**
     * 初期化処理
     *
     * @param event イベント
     * @return なし
     */
    fun init() {
        signUpUIState.value = SignUpUIState(imageUri = null)
        loginUIState.value = LoginUIState()
        signUpUsernameScreenValidationPassed.value = false
        signUpAllValidationPassed.value = false
        signUpUsernamePassed.value = false
        loginEmailPassed.value = false
        loginAllValidationPassed.value = false
    }

    /**
     * 新規作成イベント。各イベントごとに処理を分ける。
     *
     * @param event イベント
     * @return なし
     */
    fun onSignUpEvent(event: SignUpUIEvent) {
        validateSignUpDataWithRules()
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
        validateLoginDataWithRules()
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
    private fun validateSignUpDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = signUpUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = signUpUIState.value.password
        )

        val password2Result = Validator.validatePassword(
            password = signUpUIState.value.password2
        )

        val usernameResult = Validator.validateUsername(
            username = signUpUIState.value.username
        )

        val ageResult = Validator.validateAge(
            age = signUpUIState.value.age
        )

        val addressResult = Validator.validateAddress(
            address = signUpUIState.value.address
        )

        signUpUIState.value = signUpUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            password2Error = password2Result.status,
            usernameError =  usernameResult.status,
            ageError =  ageResult.status,
            addressError =  addressResult.status,
        )

        signUpUsernameScreenValidationPassed.value = usernameResult.status && ageResult.status && addressResult.status
        signUpAllValidationPassed.value = emailResult.status && passwordResult.status && password2Result.status
        signUpUsernamePassed.value = usernameResult.status
    }

    /**
     * ログインの各イベントごとのエラー処理
     *
     * @return なし
     */
    private fun validateLoginDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status,
        )

        loginAllValidationPassed.value = emailResult.status && passwordResult.status
        loginEmailPassed.value = emailResult.status
    }

    /**
     * 現在ユーザー情報を取得
     *
     * @return なし
     */
    fun fetchCurrentUser() {
        progress.value = true

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            println(Setting.failureFetchUID)
            progress.value = false
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
            }
            .addOnFailureListener { exception ->
                handleError(title = "", text = Setting.failureFetchStorePoint, exception = exception)
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
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            handleError(title = "", text = Setting.failureFetchUser, exception = null)
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener {
                PostOfficeAppRouter.navigateTo(Screen.ConfirmEmailScreen)
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureSendEmail, exception = it)
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
                    val user = it.result.user
                    user?.let {
                        if(it.isEmailVerified) {
                            PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                        } else {
                            PostOfficeAppRouter.navigateTo(Screen.NotConfirmEmailScreen)
                        }
                    }
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
     * ログイン（メールアドレス認証含む）
     *
     * @return なし
     */
    fun handleLoginWithConfirmEmail() {
        val email = signUpUIState.value.email
        val password = signUpUIState.value.password

        progress.value = true

        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progress.value = false

                val user = it.result.user
                user?.let {
                    if(!it.isEmailVerified) {
                        handleError(title = "", text = "メールアドレスの認証が完了していません。" +
                                "\n再度メールを送信する場合は、下の「メールを再送する」を押してください。", exception = null)
                        return@let
                    }

                    // メールアドレス認証済み処理
                    val data = hashMapOf<String, Any>(
                        FirebaseConstants.isConfirmEmail to true,
                    )
                    updateUser(document = it.uid, data = data)
                    PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
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
                        handleAlert(title = "", text = "メール認証済みです。")
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
     * 入力したメールアドレスにパスワード再設定リンクを送る
     *
     * @return なし
     */
    fun handleSendResetPasswordLink() {
        val email = loginUIState.value.email

        FirebaseAuth
            .getInstance()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                handleAlert(title = "", text = "入力したメールアドレスにパスワード再設定用のURLを送信しました。")
                PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureSendEmail, exception = null)
            }
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
                    PostOfficeAppRouter.navigateTo(Screen.CompulsionEntryScreen)
                }
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
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
            FirebaseConstants.isConfirmEmail to false,
            FirebaseConstants.isFirstLogin to false,
            FirebaseConstants.isStore to false,
            FirebaseConstants.isOwner to false,
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
     * 店舗ポイント情報を更新
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
     * 画像を削除
     *
     * @param uid UID
     * @return なし
     */
    fun deleteImage(uid: String) {
        FirebaseStorage
            .getInstance()
            .reference
            .child(uid)
            .delete()
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureDeleteImage, exception = it)
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
     * QRコード読み取り処理
     *
     * @param chatUserUid 読み取ったQRコードのUID
     * @return なし
     */
    private fun handleScan(chatUserUid: String) {
        // 同アカウントのQRコードを読み取ってしまった場合、エラーを発動。
        if(currentUser.value.uid == chatUserUid) {
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
                        // 店舗ポイント情報を取得
                        FirebaseFirestore
                            .getInstance()
                            .collection(FirebaseConstants.storePoints)
                            .document(currentUser.value.uid)
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

    private fun divideScanProcess() {
        // 店舗ポイントアカウントの場合
        if(chatUser.value?.isStore == true) {
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
        val intCurrentUserMoney = currentUser.value.money.toInt()
        val intGetPoint = getPoint.value.toInt()

        // 残高に取得ポイントを足す
        val calculatedCurrentUserMoney = intCurrentUserMoney + intGetPoint

        // 自身のユーザー情報を更新
        val userData = hashMapOf<String, Any>(
            FirebaseConstants.money to calculatedCurrentUserMoney.toString(),
        )
        updateUser(document = currentUser.value.uid, data = userData)

        // 店舗ポイント情報を更新
        val storePointData = hashMapOf<String, Any>(
            FirebaseConstants.uid to (chatUser.value?.uid ?: ""),
            FirebaseConstants.email to (chatUser.value?.email ?: ""),
            FirebaseConstants.profileImageUrl to (chatUser.value?.profileImageUrl ?: ""),
            FirebaseConstants.getPoint to getPoint.value,
            FirebaseConstants.username to (chatUser.value?.username ?: ""),
            FirebaseConstants.date to dateFormat(LocalDate.now())
        )

        chatUser.value?.let {
            persistStorePoint(
                document1 = currentUser.value.uid,
                document2 = it.uid,
                data = storePointData
            )
        }
    }

    /**
     * 送ポイント処理
     *
     * @return なし
     */
    fun handleSendPoint() {
        chatUser.value?.let {
            val chatUserMoney = it.money.toInt()
            val currentUserMoney = currentUser.value.money.toInt()
            val sendPayText = sendPayText.value.toInt()

            // TODO: -  互いに友達登録していない場合、新規友達登録をする。

            // 各ユーザーの残高を計算
            val calculatedChatUserMoney = chatUserMoney + sendPayText
            val calculatedCurrentUserMoney = currentUserMoney - sendPayText

            // 各ユーザーの残高が0を下回る場合、アラートを発動
            if((calculatedChatUserMoney < 0) || (calculatedCurrentUserMoney < 0)) {
                handleError(title = "", text = "入力数値が残ポイントを超えています。", exception = null)
                return
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
            updateUser(document = currentUser.value.uid, data = userData)

            PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
        }
    }

    fun handleUpdateImage(imageUri: Uri?) {
        progress.value = true
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        // 画像削除
        deleteImage(uid = uid.toString())

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
}