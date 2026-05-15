package com.dear.common.exception

import java.time.LocalDateTime

/**
 * JSON body returned by `GlobalExceptionHandler` for all handled exceptions.
 */
data class ErrorResponse(
    val code: String,
    val message: String,
    val detail: String?,
    val timestamp: LocalDateTime,
) {
    companion object {
        fun of(errorType: ErrorType, detail: String?): ErrorResponse = ErrorResponse(
            code = errorType.code,
            message = errorType.message,
            detail = detail,
            timestamp = LocalDateTime.now(),
        )
    }
}
