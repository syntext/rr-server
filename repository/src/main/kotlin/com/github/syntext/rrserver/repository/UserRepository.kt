package com.github.syntext.rrserver.repository

import com.github.syntext.rrserver.enumeration.UserRoleType
import com.github.syntext.rrserver.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(
		nativeQuery = true,
		value = "INSERT INTO user_roles (user_id,authority) VALUES (:userId,:role) ON CONFLICT (user_id,authority) DO NOTHING"
	)
	fun grand(@Param("userId") userId: UUID, @Param("role") role: UserRoleType)

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(nativeQuery = true, value = "DELETE FROM user_roles WHERE (user_id=:userId AND authority=:role)")
	fun revoke(@Param("userId") userId: UUID, @Param("role") role: UserRoleType)
}
