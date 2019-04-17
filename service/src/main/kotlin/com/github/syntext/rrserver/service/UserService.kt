package com.github.syntext.rrserver.service

import com.github.syntext.rrserver.enumeration.UserRoleType
import com.github.syntext.rrserver.enumeration.UserRoleType.ROLE_AUTHENTICATED
import com.github.syntext.rrserver.model.User
import com.github.syntext.rrserver.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*


@Service
class UserService(private val userRepository: UserRepository) {
	private var log = LoggerFactory.getLogger(UserService::class.java)

	private val BCRYPT_PREFIX = "{bcrypt}"

	// --[ LIST ]-------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	fun list(withHistory: Boolean): Set<User> {
		val now = ZonedDateTime.now()
		val result: MutableSet<User> = mutableSetOf()
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
			model.password = encryptPassword(model.password)
		}
		model.lastModified = model.createdOn
		userRepository.saveAndFlush(model)
		grant(model.id, ROLE_AUTHENTICATED)
	}

	// --[ READ ]-------------------------------------------------------------------------------------------------------
	@Transactional(readOnly = true)
	fun available(email: String): Boolean {
		return !userRepository.existsByEmail(email)
	}

	@Transactional(readOnly = true)
	fun read(email: String): User? {
		return userRepository.findByEmail(email)
	}

	@Transactional(readOnly = true)
	fun read(id: UUID): User? {
		return userRepository.findByIdOrNull(id)
	}

	@Transactional(readOnly = true)
	fun available(email: String, id: UUID): Boolean {
		return userRepository.existsByEmailAndId(email, id)
	}


	// --[ UPDATE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun update(model: User): Boolean {
		val user = userRepository.findByIdOrNull(model.id)

		if (user != null) {
			model.roles = user.roles
			model.lastModified = ZonedDateTime.now()
			userRepository.saveAndFlush(model)
			return true
		}

		return false
	}

	@Transactional
	fun updatePassword(userId: UUID, newPassword: String): Boolean {
		val user = userRepository.findByIdOrNull(userId)
		user?.let {
			it.password = encryptPassword(newPassword)
			it.roles = user.roles
			it.lastModified = ZonedDateTime.now()
			userRepository.saveAndFlush(user)
			return true
		}
		return false
	}

	// --[ DELETE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun deactivate(userId: UUID, since: ZonedDateTime) {
		val user = userRepository.findByIdOrNull(userId)
		user?.let {
			user.disabledOn = since
			userRepository.saveAndFlush(user)
		}
	}

	@Transactional
	fun activate(brandId: UUID, userId: UUID) {
		val user = userRepository.findByIdOrNull(userId)
		user?.let {
			it.disabledOn = null
			userRepository.saveAndFlush(it)
		}
	}

	@Transactional
	fun delete(id: UUID) {
		userRepository.deleteById(id)
	}

	// ==[ ROLE ]=======================================================================================================
	@Transactional
	fun grant(userId: UUID, role: UserRoleType) {
		userRepository.grand(userId, role)
	}

	@Transactional
	fun revoke(userId: UUID, role: UserRoleType) {
		userRepository.revoke(userId, role)
	}

	// --[ LOGIC ]------------------------------------------------------------------------------------------------------
	fun encryptPassword(password: String): String {
		return BCRYPT_PREFIX + BCryptPasswordEncoder().encode(password)
	}
}

