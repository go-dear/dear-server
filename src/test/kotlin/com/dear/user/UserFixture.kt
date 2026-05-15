package com.dear.user

import com.dear.user.domain.User
import com.dear.user.domain.UserRole

/**
 * The single sanctioned place to call the [User] constructor directly.
 * Production code must go through [User.create].
 */
object UserFixture {

    const val DEFAULT_REQUESTER: Long = 1L

    fun aUser(
        id: Long? = null,
        email: String = "alice@example.com",
        nickname: String = "Alice",
        role: UserRole = UserRole.USER,
        requester: Long = DEFAULT_REQUESTER,
    ): User = User(
        email = email,
        nickname = nickname,
        role = role,
        requester = requester,
        id = id,
    )
}
