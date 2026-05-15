package com.dear.user.domain

import com.dear.common.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, length = 30)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: UserRole,

    requester: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
) : BaseEntity(createdBy = requester, updatedBy = requester) {

    fun rename(newNickname: String, requester: Long) {
        this.nickname = newNickname
        auditUpdatedBy(requester)
    }

    fun changeRole(newRole: UserRole, requester: Long) {
        this.role = newRole
        auditUpdatedBy(requester)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    companion object {
        /**
         * Preferred way to instantiate a new User from domain code.
         * Calling the constructor directly is allowed but flagged in PR review;
         * test fixtures (UserFixture) are the only sanctioned exception.
         */
        fun create(
            email: String,
            nickname: String,
            role: UserRole,
            requester: Long,
        ): User = User(
            email = email,
            nickname = nickname,
            role = role,
            requester = requester,
            id = null,
        )
    }
}
