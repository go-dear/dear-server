package com.dear.user.service

import com.dear.common.exception.BadRequestException
import com.dear.common.exception.NotFoundException
import com.dear.common.exception.UserErrorType
import com.dear.user.UserFixture
import com.dear.user.domain.UserRole
import com.dear.user.repository.FakeUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserServiceTest {

    private lateinit var users: FakeUserRepository
    private lateinit var service: UserService

    private val requester: Long = UserFixture.DEFAULT_REQUESTER

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        service = UserService(users)
    }

    @Test
    @DisplayName("register: 새 유저는 ID가 부여되고 모델로 반환된다")
    fun `register assigns id and persists`() {
        val model = service.register(
            email = "alice@example.com",
            nickname = "Alice",
            requester = requester,
        )

        assertThat(model.id).isNotNull()
        assertThat(model.email).isEqualTo("alice@example.com")
        assertThat(model.nickname).isEqualTo("Alice")
        assertThat(model.role).isEqualTo(UserRole.USER)
        assertThat(users.count()).isEqualTo(1L)
    }

    @Test
    @DisplayName("register: 같은 이메일은 BadRequestException(UserErrorType.DUPLICATE_EMAIL)")
    fun `register fails on duplicate email`() {
        service.register("alice@example.com", "Alice", requester)

        assertThatThrownBy { service.register("alice@example.com", "Alicia", requester) }
            .isInstanceOfSatisfying(BadRequestException::class.java) { ex ->
                assertThat(ex.errorType).isEqualTo(UserErrorType.DUPLICATE_EMAIL)
            }
        assertThat(users.count()).isEqualTo(1L)
    }

    @Test
    @DisplayName("findById: 저장된 유저를 모델로 반환")
    fun `findById returns persisted user`() {
        val saved = service.register("alice@example.com", "Alice", requester)

        val found = service.findById(saved.id)
        assertThat(found.id).isEqualTo(saved.id)
        assertThat(found.email).isEqualTo(saved.email)
    }

    @Test
    @DisplayName("findById: 없는 ID는 NotFoundException")
    fun `findById throws when missing`() {
        assertThatThrownBy { service.findById(999L) }
            .isInstanceOf(NotFoundException::class.java)
    }

    @Test
    @DisplayName("findByEmailOrNull: 일치하는 모델 반환, 없으면 null")
    fun `findByEmailOrNull returns matching model or null`() {
        service.register("alice@example.com", "Alice", requester)

        assertThat(service.findByEmailOrNull("alice@example.com")?.nickname).isEqualTo("Alice")
        assertThat(service.findByEmailOrNull("missing@example.com")).isNull()
    }
}
