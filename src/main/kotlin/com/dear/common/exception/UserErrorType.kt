package com.dear.common.exception

enum class UserErrorType(
    override val code: String,
    override val message: String,
) : ErrorType {
    DUPLICATE_EMAIL("USER_001", "Email already exists"),
}
