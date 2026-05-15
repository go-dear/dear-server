package com.dear.common.exception

enum class CommonErrorType(
    override val code: String,
    override val message: String,
) : ErrorType {
    UNAUTHORIZED("COMMON_AUTH_001", "Unauthorized"),
    MISSING_AUTH_HEADER("COMMON_AUTH_002", "X-User-Id header is required"),
    INVALID_AUTH_HEADER("COMMON_AUTH_003", "X-User-Id header must be a numeric user id"),
    UNKNOWN_USER("COMMON_AUTH_004", "Unknown user id"),
    FORBIDDEN("COMMON_AUTH_005", "Insufficient permission"),
    NOT_FOUND("COMMON_RES_001", "Resource not found"),
    INTERNAL_ERROR("COMMON_SYS_001", "Internal error"),
}
