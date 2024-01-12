package com.example.sheeba.util

object Validator {
    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length >= 6)
        )
    }

    fun validateUsername(username: String): ValidationResult {
        return ValidationResult(
            (!username.isNullOrEmpty() && username.length >= 1)
        )
    }

    fun validateAge(age: String): ValidationResult {
        return ValidationResult(
            (!age.isNullOrEmpty() && age.length >= 1)
        )
    }

    fun validateAddress(address: String): ValidationResult {
        return ValidationResult(
            (!address.isNullOrEmpty() && address.length >= 1)
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)