package com.dear.user.controller

import com.dear.common.web.CurrentUserId
import com.dear.user.dto.RegisterUserRequest
import com.dear.user.dto.UserResponse
import com.dear.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/v1")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterUserRequest,
        currentUserId: CurrentUserId,
    ): UserResponse = UserResponse.from(
        userService.register(
            email = request.email,
            nickname = request.nickname,
            requester = currentUserId.userId,
        ),
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserResponse =
        UserResponse.from(userService.findById(id))
}
