package com.dear.common.web

import com.dear.common.exception.CommonErrorType
import com.dear.common.exception.UnauthorizedException
import com.dear.user.repository.UserRepository
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.stereotype.Component

/**
 * Resolves an `X-User-Id` header value to a `CurrentUserId` with role.
 *
 * The role of a user does not change between ADMIN ↔ USER, so the lookup is
 * cached indefinitely (LRU by maximumSize) — no timed expiry. Cache invalidation
 * is not needed pre-launch; when an admin-driven role change feature lands the
 * service mutating role must call `invalidate(userId)`.
 */
@Component
class CurrentUserResolver(
    private val users: UserRepository,
) {
    private val cache: LoadingCache<Long, CurrentUserId> = Caffeine.newBuilder()
        .maximumSize(MAX_ENTRIES)
        .build { userId ->
            val user = users.findById(userId).orElseThrow {
                UnauthorizedException(CommonErrorType.UNKNOWN_USER, detail = "id=$userId")
            }
            CurrentUserId(userId = requireNotNull(user.id), role = user.role)
        }

    fun resolve(userId: Long): CurrentUserId = cache.get(userId)

    fun invalidate(userId: Long) {
        cache.invalidate(userId)
    }

    companion object {
        const val MAX_ENTRIES: Long = 10_000
    }
}
