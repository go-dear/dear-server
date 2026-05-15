package com.dear.common.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Base class for all JPA entities. Provides the four audit columns required
 * by .claude/docs/policy/audit-columns.md.
 *
 * Time columns (createdAt, updatedAt) are populated automatically by Spring
 * Data JPA Auditing via @CreatedDate / @LastModifiedDate.
 *
 * User columns (createdBy, updatedBy) are populated by domain code: the entity
 * subclass forwards `requester` through its primary constructor, and update
 * methods call `auditUpdatedBy(requester)`. AuditorAware is intentionally not
 * used — keeping the actor flow explicit in code.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    createdBy: Long,
    updatedBy: Long,
) {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @Column(name = "created_by", nullable = false, updatable = false)
    var createdBy: Long = createdBy
        protected set

    @Column(name = "updated_by", nullable = false)
    var updatedBy: Long = updatedBy
        protected set

    /** Subclasses call this from update-style domain methods. */
    protected fun auditUpdatedBy(requester: Long) {
        this.updatedBy = requester
    }
}
