package com.github.syntext.rrserver.repository

import com.github.syntext.rrserver.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

	@Transactional(readOnly = true)
	fun findByEmail(email: String): User?

	@Transactional(readOnly = true)
	fun existsByEmail(email: String): Boolean

	@Transactional(readOnly = true)
	fun existsByEmailAndId(email: String, id: UUID): Boolean

	@Transactional(readOnly = true)
	fun findByDisabledOnIsNullOrDisabledOnGreaterThanOrderByCreatedOnDesc(disabledOn: ZonedDateTime): Set<User>

	@Transactional(readOnly = true)
	fun findByDisabledOnLessThanEqualOrderByCreatedOnDesc(disabledOn: ZonedDateTime): Set<User>
}
