package com.dear.common.persistence

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.FluentQuery
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Function

/**
 * Test-only in-memory base for [JpaRepository] fakes.
 *
 * Implements the common CRUD subset against an in-memory map. Derived queries
 * (findByEmail, findByXxx, ...) are implemented by each concrete fake.
 *
 * Why this exists: production repositories extend [JpaRepository] for derived
 * queries / paging. To swap them out in unit tests without Spring or a DB, the
 * fake must satisfy the same contract. This base covers the boilerplate so
 * each fake only writes the domain-specific bits.
 *
 * Methods not commonly needed in unit tests (QueryByExample, batch flush
 * semantics) intentionally throw — call sites that hit them are usually a
 * smell and should be revisited.
 */
abstract class InMemoryRepository<T : Any, ID : Any> : JpaRepository<T, ID> {

    protected val store: MutableMap<ID, T> = ConcurrentHashMap()
    private val sequence = AtomicLong(0)

    /** How to read the ID off an entity (null = unsaved). */
    protected abstract fun extractId(entity: T): ID?

    /** How to assign a newly generated ID to an entity. */
    protected abstract fun withId(entity: T, id: ID): T

    /** Default ID generator assumes [Long]. Override for other ID types. */
    @Suppress("UNCHECKED_CAST")
    protected open fun nextId(): ID = sequence.incrementAndGet() as ID

    // ---- CrudRepository ----

    override fun <S : T> save(entity: S): S {
        val existingId = extractId(entity)
        val id = existingId ?: nextId()
        @Suppress("UNCHECKED_CAST")
        val stored = if (existingId == null) withId(entity, id) as S else entity
        store[id] = stored
        return stored
    }

    override fun <S : T> saveAll(entities: Iterable<S>): List<S> = entities.map(::save)

    override fun findById(id: ID): Optional<T> = Optional.ofNullable(store[id])

    override fun existsById(id: ID): Boolean = store.containsKey(id)

    override fun findAll(): List<T> = store.values.toList()

    override fun findAllById(ids: Iterable<ID>): List<T> = ids.mapNotNull { store[it] }

    override fun count(): Long = store.size.toLong()

    override fun deleteById(id: ID) {
        store.remove(id)
    }

    override fun delete(entity: T) {
        extractId(entity)?.let { store.remove(it) }
    }

    override fun deleteAllById(ids: Iterable<ID>) = ids.forEach(::deleteById)

    override fun deleteAll(entities: Iterable<T>) = entities.forEach(::delete)

    override fun deleteAll() = store.clear()

    // ---- JpaRepository batch / flush — no-op semantics in memory ----

    override fun flush() { /* no-op */ }

    override fun <S : T> saveAndFlush(entity: S): S = save(entity)

    override fun <S : T> saveAllAndFlush(entities: Iterable<S>): List<S> = saveAll(entities)

    override fun deleteAllInBatch(entities: Iterable<T>) = deleteAll(entities)

    override fun deleteAllByIdInBatch(ids: Iterable<ID>) = deleteAllById(ids)

    override fun deleteAllInBatch() = deleteAll()

    override fun getReferenceById(id: ID): T =
        store[id] ?: throw EntityNotFoundException(id.toString())

    @Deprecated("Use getReferenceById", ReplaceWith("getReferenceById(id)"))
    override fun getOne(id: ID): T = getReferenceById(id)

    @Deprecated("Use getReferenceById", ReplaceWith("getReferenceById(id)"))
    override fun getById(id: ID): T = getReferenceById(id)

    // ---- PagingAndSortingRepository — basic, no sort semantics ----

    override fun findAll(sort: Sort): List<T> = findAll()

    override fun findAll(pageable: Pageable): Page<T> {
        val all = findAll()
        val from = minOf(pageable.offset.toInt(), all.size)
        val to = minOf(from + pageable.pageSize, all.size)
        return PageImpl(all.subList(from, to), pageable, all.size.toLong())
    }

    // ---- QueryByExampleExecutor — not supported by default ----

    override fun <S : T> findOne(example: Example<S>): Optional<S> = unsupported("findOne(Example)")

    override fun <S : T> findAll(example: Example<S>): List<S> = unsupported("findAll(Example)")

    override fun <S : T> findAll(example: Example<S>, sort: Sort): List<S> =
        unsupported("findAll(Example, Sort)")

    override fun <S : T> findAll(example: Example<S>, pageable: Pageable): Page<S> =
        unsupported("findAll(Example, Pageable)")

    override fun <S : T> count(example: Example<S>): Long = unsupported("count(Example)")

    override fun <S : T> exists(example: Example<S>): Boolean = unsupported("exists(Example)")

    override fun <S : T, R : Any> findBy(
        example: Example<S>,
        queryFunction: Function<FluentQuery.FetchableFluentQuery<S>, R>,
    ): R = unsupported("findBy(Example, FluentQuery)")

    private fun unsupported(method: String): Nothing =
        throw UnsupportedOperationException(
            "$method is not supported by InMemoryRepository — override in fake if your test needs it.",
        )
}
