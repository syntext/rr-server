package com.github.syntext.rrserver.service.security.authentication

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

@Service
class BruteForcePreventionService() {

	private val log = LoggerFactory.getLogger(BruteForcePreventionService::class.java)
	private val MAX_ATTEMPT = 15

	lateinit var attempts: Cache<String, Int>

	init {
		attempts = Caffeine.newBuilder()
			.expireAfterWrite(1, TimeUnit.DAYS)
			.maximumSize(100)
			.build()
	}

	fun getIp(request: HttpServletRequest): String {
		val ipAddress = request.getHeader("X-FORWARDED-FOR")
		ipAddress?.let { return ipAddress }
		return request.remoteAddr
	}

	fun loginSucceeded(key: String) {
		log.trace("loginSucceeded [{}]", key)
		attempts.invalidate(key)
	}

	fun loginFailed(key: String) {
		log.trace("loginFailed [{}]", key)
		var attempt = attempts.getIfPresent(key)
		if (attempt == null) {
			attempt = 0
		}
		attempt++
		attempts.put(key, attempt)
	}

	fun isBlocked(key: String): Boolean {
		var result = false
		val attempts = attempts.getIfPresent(key)
		if (attempts != null) {
			result = attempts >= MAX_ATTEMPT
		}
		log.trace("isBlocked [{}]: {}", key, result)
		return result
	}

	fun getAttempts(): Map<String, Int> {
		return attempts.asMap()
	}
}
