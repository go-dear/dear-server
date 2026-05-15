package com.dear.common.exception

/**
 * Thrown when the requester is identified but lacks permission for the action.
 * Maps to HTTP 403.
 */
open class ForbiddenException(
    val errorType: ErrorType = CommonErrorType.FORBIDDEN,
    detail: String? = null,
) : RuntimeException(detail ?: errorType.message)
