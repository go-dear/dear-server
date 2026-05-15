package com.dear.common.exception

/**
 * Thrown when a requested resource does not exist. Defaults to
 * `CommonErrorType.NOT_FOUND`; domain code can pass a more specific
 * ErrorType when meaningful. `detail` carries dynamic context such as
 * the lookup id.
 */
open class NotFoundException(
    val errorType: ErrorType = CommonErrorType.NOT_FOUND,
    detail: String? = null,
) : RuntimeException(detail ?: errorType.message)
