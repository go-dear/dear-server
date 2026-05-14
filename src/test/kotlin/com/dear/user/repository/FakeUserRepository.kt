package com.dear.user.repository

import com.dear.common.persistence.InMemoryRepository
import com.dear.user.domain.User

class FakeUserRepository : UserRepository, InMemoryRepository<User, Long>() {

    override fun extractId(entity: User): Long? = entity.id

    override fun withId(entity: User, id: Long): User {
        entity.id = id
        return entity
    }

    override fun findByEmail(email: String): User? =
        store.values.firstOrNull { it.email == email }

    override fun existsByEmail(email: String): Boolean =
        store.values.any { it.email == email }
}
