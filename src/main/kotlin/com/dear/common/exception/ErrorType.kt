package com.dear.common.exception

/**
 * Sealed marker for typed error codes.
 *
 * `BadRequestException` takes a domain-specific ErrorType (e.g., `UserErrorType`).
 * Non-BadRequest exceptions (Unauthorized, Forbidden, NotFound, ...) default to
 * `CommonErrorType`.
 *
 * **Constraint**: all implementations must live in this `com.dear.common.exception`
 * package (Kotlin sealed restriction). Each domain's ErrorType enum is a separate
 * file under this package — naming carries the domain (e.g., `UserErrorType`,
 * `MedicationErrorType`).
 */
sealed interface ErrorType {
    val code: String
    val message: String
}
