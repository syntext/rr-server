package com.github.syntext.rrserver.service

import com.github.syntext.rrserver.enumeration.UserRoleType
import com.github.syntext.rrserver.enumeration.UserRoleType.ROLE_AUTHENTICATED
import com.github.syntext.rrserver.model.User
import com.github.syntext.rrserver.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private const val BCRYPT_PREFIX = "{bcrypt}"
private val LOG = KotlinLogging.logger {}

@Service
class UserService(private val userRepository: UserRepository) {

	// --[ LIST ]-------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	fun list(withHistory: Boolean): Set<User> {
		val now = ZonedDateTime.now()
		val result = mutableListOf<User>()
		result.addAll(userRepository.findByDisabledOnIsNullOrDisabledOnGreaterThanOrderByCreatedOnDesc(now))
		if (withHistory) {
			result.addAll(userRepository.findByDisabledOnLessThanEqualOrderByCreatedOnDesc(now))
		}
		return result.toSet()
	}

	// --[ CREATE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun create(brandId: UUID, model: User, locale: Locale) {
		if (model.password.isNotBlank()) {
			model.password = hashPassword(model.password)
		}
		model.lastModified = model.createdOn
		userRepository.saveAndFlush(model)
		grant(model.id, ROLE_AUTHENTICATED)
	}

	// --[ READ ]-------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	fun available(email: String) = !userRepository.existsByEmail(email)

	@Transactional(readOnly = true)
	fun read(email: String) = userRepository.findByEmail(email)

	@Transactional(readOnly = true)
	fun read(id: UUID) = userRepository.findByIdOrNull(id)

	@Transactional(readOnly = true)
	fun available(email: String, id: UUID) = userRepository.existsByEmailAndId(email, id)

	// --[ UPDATE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun update(model: User): Boolean {
		userRepository.findByIdOrNull(model.id)?.let {
			model.password = it.password
			model.roles = it.roles
			model.lastModified = ZonedDateTime.now()
			userRepository.saveAndFlush(model)
			return true
		}

		return false
	}

	@Transactional
	fun updatePassword(userId: UUID, newPassword: String): Boolean {
		userRepository.findByIdOrNull(userId)?.let {
			it.password = hashPassword(newPassword)
			it.lastModified = ZonedDateTime.now()
			userRepository.saveAndFlush(it)
			return true
		}
		return false
	}

	// --[ DELETE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun deactivate(userId: UUID, since: ZonedDateTime) {
		userRepository.findByIdOrNull(userId)?.let {
			it.disabledOn = since
			userRepository.saveAndFlush(it)
		}
	}

	@Transactional
	fun activate(brandId: UUID, userId: UUID) {
		userRepository.findByIdOrNull(userId)?.let {
			it.disabledOn = null
			userRepository.saveAndFlush(it)
		}
	}

	@Transactional
	fun delete(id: UUID) = userRepository.deleteById(id)

	// ==[ ROLE ]=======================================================================================================
	@Transactional
	fun grant(userId: UUID, role: UserRoleType) = userRepository.grant(userId, role)

	@Transactional
	fun revoke(userId: UUID, role: UserRoleType) = userRepository.revoke(userId, role)

	// --[ LOGIC ]------------------------------------------------------------------------------------------------------
	fun hashPassword(password: String) = BCRYPT_PREFIX + BCryptPasswordEncoder().encode(password)
}
