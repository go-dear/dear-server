package com.dear.user.service

import com.dear.common.exception.BadRequestException
import com.dear.common.exception.NotFoundException
import com.dear.common.exception.UserErrorType
import com.dear.user.domain.User
import com.dear.user.domain.UserRole
import com.dear.user.model.UserModel
import com.dear.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val users: UserRepository,
) {

    @Transactional
    fun register(email: String, nickname: String, requester: Long): UserModel {
        if (users.existsByEmail(email)) {
            throw BadRequestException(UserErrorType.DUPLICATE_EMAIL, detail = "email=$email")
        }
        val saved = users.save(
            User.create(
                email = email,
                nickname = nickname,
                role = UserRole.USER,
                requester = requester,
            ),
        )
        return UserModel.from(saved)
    }

    fun findById(id: Long): UserModel =
        UserModel.from(
            users.findById(id).orElseThrow { NotFoundException(detail = "User not found: id=$id") },
        )

    fun findByEmailOrNull(email: String): UserModel? =
        users.findByEmail(email)?.let(UserModel::from)
}
