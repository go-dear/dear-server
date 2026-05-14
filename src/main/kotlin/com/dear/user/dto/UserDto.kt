package com.dear.user.dto

import com.dear.user.domain.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class RegisterUserRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(max = 30, message = "닉네임은 30자 이하여야 합니다")
    val nickname: String,
)

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = requireNotNull(user.id) { "Persisted user must have id" },
            email = user.email,
            nickname = user.nickname,
            createdAt = user.createdAt,
        )
    }
}
