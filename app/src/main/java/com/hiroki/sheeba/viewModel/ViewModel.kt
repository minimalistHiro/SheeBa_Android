package com.hiroki.sheeba.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.FirebaseFirestore
import com.hiroki.sheeba.app.PostOfficeAppRouter
import com.hiroki.sheeba.app.Screen
import com.hiroki.sheeba.data.LoginUIEvent
import com.hiroki.sheeba.data.LoginUIState
import com.hiroki.sheeba.data.SignUpUIEvent
import com.hiroki.sheeba.data.SignUpUIState
import com.hiroki.sheeba.model.ChatUser
import com.hiroki.sheeba.util.Setting
import com.hiroki.sheeba.util.Validator

class ViewModel: ViewModel() {
    private val TAG = ViewModel::class.simpleName
    var signUpUIState = mutableStateOf(SignUpUIState())
    var signUpUsernameValidationPassed = mutableStateOf(false)
    var signUpAllValidationPassed = mutableStateOf(false)
    var loginUIState = mutableStateOf(LoginUIState())
    var loginAllValidationPassed = mutableStateOf(false)
    var progress = mutableStateOf(false)

//    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()
    var currentUser = mutableStateOf(ChatUser())

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
            usernameError =  usernameResult.status,
            ageError =  ageResult.status,
            addressError =  addressResult.status,
        )

        signUpUsernameValidationPassed.value = usernameResult.status && ageResult.status && addressResult.status
        signUpAllValidationPassed.value = emailResult.status && passwordResult.status
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
            .collection(Setting.users)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.toObject(ChatUser::class.java)?.let {
                        currentUser = mutableStateOf(it)
                    }
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
                progress.value = false
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                progress.value = false
            }
    }

    /**
     * 新規作成
     *
     * @param email メールアドレス
     * @param password パスワード
     * @param username ユーザー名
     * @param age 年代
     * @param address 住所
     * @return なし
     */
    private fun createNewAccount(email: String, password: String, username: String, age: String, address: String) {
        progress.value = true

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
                Log.d(TAG, "Exception = ${it.localizedMessage}")
                progress.value = false
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
                Log.d(TAG, "${it.isSuccessful}")

                if(it.isSuccessful) {
                    PostOfficeAppRouter.navigateTo(Screen.ContentScreen)
                }
                progress.value = false
            }
            .addOnFailureListener {
                Log.d(TAG, "${it.localizedMessage}")
                progress.value = false
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
                PostOfficeAppRouter.navigateTo(Screen.EntryScreen)
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
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
            println(Setting.failureFetchUID)
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
            .collection(Setting.users)
            .document(uid)
            .set(user)
    }
}