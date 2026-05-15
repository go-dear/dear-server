package com.dear.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Enables Spring Data JPA Auditing for @CreatedDate / @LastModifiedDate.
 *
 * No AuditorAware bean is registered: created_by / updated_by are set by
 * domain code (see BaseEntity and .claude/docs/policy/audit-columns.md).
 */
@Configuration
@EnableJpaAuditing
class JpaAuditingConfig
