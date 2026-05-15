package com.dear.common.exception

/**
 * Thrown when the request lacks valid identification.
 * Maps to HTTP 401.
 */
open class UnauthorizedException(
    val errorType: ErrorType = CommonErrorType.UNAUTHORIZED,
    detail: String? = null,
) : RuntimeException(detail ?: errorType.message)
