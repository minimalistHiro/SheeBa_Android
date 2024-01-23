package com.hiroki.sheeba.viewModel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.FirebaseFirestore
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
import java.util.concurrent.Executors

class ViewModel: ViewModel() {
    private val TAG = ViewModel::class.simpleName
    var signUpUIState = mutableStateOf(SignUpUIState())
    var signUpUsernameValidationPassed = mutableStateOf(false)
    var signUpAllValidationPassed = mutableStateOf(false)
    var loginUIState = mutableStateOf(LoginUIState())
    var loginAllValidationPassed = mutableStateOf(false)
    var progress = mutableStateOf(false)

    // DB
//    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()
    var currentUser = mutableStateOf(ChatUser())
    var chatUser = mutableStateOf(ChatUser())
    var storePoints = mutableListOf<StorePoint>()
    var storePoint = mutableStateOf(StorePoint())

    // ダイアログ
    var isShowDialog = mutableStateOf(false)
    var dialogTitle = mutableStateOf("")
    var dialogText = mutableStateOf("")
    var isShowDialogForLogout = mutableStateOf(false)           // ログアウトへと誘導するダイアログ
    var isShowCompulsionLogoutDialog = mutableStateOf(false)    // 強制ログアウトダイアログ

    // キーボード関連
    var sendPayText = mutableStateOf("0")                       // 送金テキスト
    var isTappedAC = mutableStateOf(false)                      // ACボタンがタップされたか否か
    var getPoint = mutableStateOf("0")                          // 獲得ポイント

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
                    handleScan(chatUserUid = chatUserUid)
                },
            )
        }
    private val _qrCode = mutableStateOf<Barcode?>(null)
    val qrCode: androidx.compose.runtime.State<Barcode?> = _qrCode
    var isQrCodeScanError = mutableStateOf(false)               // QRコード読み取りエラー
    var isSameStoreScanError = mutableStateOf(false)            // 同日同店舗スキャンエラー

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

        signUpUsernameValidationPassed.value = usernameResult.status && ageResult.status && addressResult.status
        signUpAllValidationPassed.value = emailResult.status && passwordResult.status && password2Result.status
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
                Log.d(TAG, "isSuccessful = ${it.isSuccessful}")

                if(it.isSuccessful) {
                    persistUser(email = email, username = username, age = age, address = address)
                    PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                }
                progress.value = false
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureCreateAccount, exception = it)
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
                    PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                }
                progress.value = false
            }
            .addOnFailureListener {
                handleError(title = "", text = Setting.failureLogin, exception = it)
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
        // 認証情報削除
        deleteAuth()
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
    fun persistUser(email: String, username: String, age: String, address: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            handleErrorForLogout(title = "", text = Setting.failureFetchUID, exception = null)
            return
        }

        val user = ChatUser(
            uid = uid,
            email = email,
            profileImageUrl = "",
            money = Setting.newRegistrationBenefits,
            username = username,
            age = age,
            address = address,
            isConfirmEmail = false,
            isFirstLogin = false,
            isStore = false,
            isOwner = false,
        )

        FirebaseFirestore
            .getInstance()
            .collection(FirebaseConstants.users)
            .document(uid)
            .set(user)
            .addOnFailureListener {
                handleError(title = "", text = Setting.failurePersistUser, exception = it)
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
                Log.d(TAG, "true")
                return
            }

            if(sendPayText.value == "0") {
                sendPayText.value = keyboard
            } else {
                sendPayText.value += keyboard
                Log.d(TAG, "false")
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
        fetchUser(chatUserUid)
        fetchStorePoint(document1 = currentUser.value.uid, document2 = chatUserUid)

        Handler(Looper.getMainLooper()).postDelayed({
            // 店舗QRコードの場合
            if(chatUser.value.isStore) {
                handleGetPointFromStore()
            } else {
                if(!isQrCodeScanError.value) {
                    // ユーザーに送ポイント。画面遷移する.
                }
            }
        }, 100)
//                    Log.d("qrCodeAnalyzeUseCase_rawValue", qrCode.rawValue.toString())
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
        updateUser(document = chatUser.value.uid, data = userData)

        // 店舗ポイント情報を更新
        val storePointData = hashMapOf<String, Any>(
            FirebaseConstants.uid to chatUser.value.uid,
            FirebaseConstants.email to chatUser.value.email,
            FirebaseConstants.profileImageUrl to chatUser.value.profileImageUrl,
            FirebaseConstants.getPoint to getPoint,
            FirebaseConstants.username to chatUser.value.username,
        )
        persistStorePoint(
            document1 = currentUser.value.uid,
            document2 = chatUser.value.uid,
            data = storePointData
        )
    }
}