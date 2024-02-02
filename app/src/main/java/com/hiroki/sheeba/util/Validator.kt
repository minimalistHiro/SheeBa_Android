package com.hiroki.sheeba.util

object Validator {
    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }

    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty())
        )
    }

    fun validateUsername(username: String): ValidationResult {
        return ValidationResult(
            (!username.isNullOrEmpty())
        )
    }

    fun validateAge(age: String): ValidationResult {
        return ValidationResult(
            (!age.isNullOrEmpty())
        )
    }

    fun validateAddress(address: String): ValidationResult {
        return ValidationResult(
            (!address.isNullOrEmpty())
        )
    }
}

data class ValidationResult(
    val status: Boolean = false
)