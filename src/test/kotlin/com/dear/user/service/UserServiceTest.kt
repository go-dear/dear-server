package com.dear.user.service

import com.dear.user.repository.FakeUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceTest {

    private lateinit var users: FakeUserRepository
    private lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        service = UserService(users)
    }

    @Test
    @DisplayName("register: 새 유저는 ID가 부여되고 저장된다")
    fun `register assigns id and persists`() {
        val user = service.register(email = "alice@example.com", nickname = "Alice")

        assertNotNull(user.id)
        assertEquals("alice@example.com", user.email)
        assertEquals("Alice", user.nickname)
        assertEquals(1L, users.count())
    }

    @Test
    @DisplayName("register: 같은 이메일은 DuplicateEmailException")
    fun `register fails on duplicate email`() {
        service.register("alice@example.com", "Alice")

        val ex = assertThrows<DuplicateEmailException> {
            service.register("alice@example.com", "Alicia")
        }
        assertEquals("Email already exists: alice@example.com", ex.message)
        assertEquals(1L, users.count())
    }

    @Test
    @DisplayName("findById: 저장된 유저를 조회한다")
    fun `findById returns persisted user`() {
        val saved = service.register("alice@example.com", "Alice")

        val found = service.findById(saved.id!!)
        assertEquals(saved.id, found.id)
        assertEquals(saved.email, found.email)
    }

    @Test
    @DisplayName("findById: 없는 ID는 UserNotFoundException")
    fun `findById throws when missing`() {
        val ex = assertThrows<UserNotFoundException> { service.findById(999L) }
        assertEquals("User not found: id=999", ex.message)
    }

    @Test
    @DisplayName("findByEmail: 일치하는 유저 반환, 없으면 null")
    fun `findByEmail returns matching user or null`() {
        service.register("alice@example.com", "Alice")

        assertEquals("Alice", service.findByEmail("alice@example.com")?.nickname)
        assertNull(service.findByEmail("missing@example.com"))
    }
}
