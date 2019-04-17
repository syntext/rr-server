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
	private val VALID_LIMIT_HOURS = -24

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
		val user = userRepository.findById(model.id)
		model.roles = user.roles
		model.lastModified = ZonedDateTime.now()
		userRepository.saveAndFlush(model)
		return true
	}

	@Transactional
	fun updatePassword(memberId: UUID, newPassword: String): Boolean {
		var user = userRepository.findByIdOrNull(memberId)
		user?.let {
			user.password = encryptPassword(newPassword)
			user.roles = user.roles
			user.lastModified = ZonedDateTime.now()
			userRepository.saveAndFlush(user)
			return true
		}
		return false
	}

	// --[ DELETE ]-----------------------------------------------------------------------------------------------------
	@Transactional
	fun deactivate(memberId: UUID, since: ZonedDateTime) {
		val user = userRepository.findByIdOrNull(memberId)
		user?.let {
			user.disabledOn = since
			userRepository.saveAndFlush(user)
		}
	}

	@Transactional
	fun activate(brandId: UUID, memberId: UUID) {
		val user = userRepository.findByIdOrNull(memberId)
		user?.let {
			user.disabledOn = null
			userRepository.saveAndFlush(user)
		}
	}

	@Transactional
	fun delete(id: UUID) {
		userRepository.deleteById(id)
	}

	// ==[ ROLE ]=======================================================================================================
	@Transactional
	fun grant(memberId: UUID, role: UserRoleType) {
		val user = userRepository.findByIdOrNull(memberId)
		user?.let {
			user.roles.add(role)
			userRepository.saveAndFlush(user)
		}
	}

	@Transactional
	fun revoke(memberId: UUID, role: UserRoleType) {
		val user = userRepository.findByIdOrNull(memberId)
		user?.let {
			user.roles.remove(role)
			userRepository.saveAndFlush(user)
		}
	}

	// --[ LOGIC ]------------------------------------------------------------------------------------------------------
	fun encryptPassword(password: String): String {
		return BCRYPT_PREFIX + BCryptPasswordEncoder().encode(password)
	}
}

