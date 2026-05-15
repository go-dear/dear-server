package com.dear.user.model

import com.dear.user.domain.User
import com.dear.user.domain.UserRole
import java.time.LocalDateTime

/**
 * Domain DTO that crosses the service ↔ controller boundary.
 *
 * Entities never leave the service layer; this model carries the safe,
 * id-guaranteed projection of a User. Response DTOs are derived from this.
 */
data class UserModel(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(user: User): UserModel = UserModel(
            id = requireNotNull(user.id) { "Persisted user must have id" },
            email = user.email,
            nickname = user.nickname,
            role = user.role,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
    }
}
