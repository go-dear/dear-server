package com.dear.user.service

import com.dear.user.domain.User
import com.dear.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val users: UserRepository,
) {

    @Transactional
    fun register(email: String, nickname: String): User {
        if (users.existsByEmail(email)) {
            throw DuplicateEmailException(email)
        }
        return users.save(User(email = email, nickname = nickname))
    }

    fun findById(id: Long): User =
        users.findById(id).orElseThrow { UserNotFoundException(id) }

    fun findByEmail(email: String): User? = users.findByEmail(email)
}

class UserNotFoundException(id: Long) : RuntimeException("User not found: id=$id")

class DuplicateEmailException(email: String) : RuntimeException("Email already exists: $email")
