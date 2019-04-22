package com.github.syntext.rrserver.service.security.authentication

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

@Service
class BruteForcePreventionService {
	companion object {
		private val LOG = KotlinLogging.logger {}
		private const val MAX_ATTEMPT = 15
	}

	val attempts: Cache<String, Int> = Caffeine.newBuilder()
		.expireAfterWrite(1, TimeUnit.DAYS)
		.maximumSize(100)
		.build()

	fun getIp(request: HttpServletRequest): String = request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr

	fun loginSucceeded(key: String) {
		LOG.trace { "loginSucceeded [$key]" }
		attempts.invalidate(key)
	}

	fun loginFailed(key: String) {
		LOG.trace { "loginFailed [$key]" }
		var attempt = attempts.getIfPresent(key) ?: 0
		attempt++
		attempts.put(key, attempt)
	}

	fun isBlocked(key: String): Boolean {
		var result = false
		attempts.getIfPresent(key)?.let {
			result = it >= MAX_ATTEMPT
		}
		LOG.trace { "isBlocked [$key]: $result" }
		return result
	}

	fun getAttempts() = attempts.asMap()
}
