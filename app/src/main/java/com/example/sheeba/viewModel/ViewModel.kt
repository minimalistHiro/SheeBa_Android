package com.example.sheeba.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.sheeba.data.SignUpUIState
import com.example.sheeba.util.Validator
import androidx.lifecycle.ViewModel
import com.example.sheeba.data.SignUpUIEvent

class ViewModel: ViewModel() {
    private val TAG = ViewModel::class.simpleName
    var signUpUIState = mutableStateOf(SignUpUIState())
    var allValidationPassed = mutableStateOf(false)
    var progress = mutableStateOf(false)

    fun onEvent(event: SignUpUIEvent) {
        validateDateDataWithRules()
        when(event) {
            is SignUpUIEvent.EmailChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    email = event.email
                )
                printState()
            }

            is SignUpUIEvent.PasswordChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    password = event.password
                )
                printState()
            }

            is SignUpUIEvent.UsernameChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    username = event.username
                )
                printState()
            }

            is SignUpUIEvent.AgeChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    age = event.age
                )
                printState()
            }

            is SignUpUIEvent.AddressChange -> {
                signUpUIState.value = signUpUIState.value.copy(
                    address = event.address
                )
                printState()
            }

            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
            }
        }
    }

    private fun validateDateDataWithRules() {
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

        allValidationPassed.value = emailResult.status && passwordResult.status &&
                usernameResult.status && ageResult.status && addressResult.status
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, signUpUIState.value.toString())
    }

    private fun signUp() {

    }
}