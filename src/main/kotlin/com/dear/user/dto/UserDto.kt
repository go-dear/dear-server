package com.dear.user.dto

import com.dear.user.model.UserModel
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

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
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(model: UserModel): UserResponse = UserResponse(
            id = model.id,
            email = model.email,
            nickname = model.nickname,
            createdAt = model.createdAt,
        )
    }
}
