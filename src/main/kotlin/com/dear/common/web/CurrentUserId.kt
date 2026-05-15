package com.dear.common.web

import com.dear.user.domain.UserRole

/**
 * Identifier object materialized per request from the `X-User-Id` header.
 * `role` is included so authorization guards can branch without hitting the
 * repository again.
 */
data class CurrentUserId(
    val userId: Long,
    val role: UserRole,
)
