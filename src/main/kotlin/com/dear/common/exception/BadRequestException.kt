package com.dear.common.exception

/**
 * Thrown for 400 Bad Request cases. Always carries a domain-specific ErrorType
 * (e.g., `UserErrorType.DUPLICATE_EMAIL`). Optional `detail` adds dynamic context
 * to the exception message without changing the error code.
 */
open class BadRequestException(
    val errorType: ErrorType,
    detail: String? = null,
) : RuntimeException(detail ?: errorType.message)
