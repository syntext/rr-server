package com.github.syntext.rrserver.service.security.authentication

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

private val LOG = KotlinLogging.logger {}
private const val MAX_ATTEMPT = 15

@Service
class BruteForcePreventionService {
	val attempts: Cache<String, Int> = Caffeine.newBuilder()
		.expireAfterWrite(1, TimeUnit.DAYS)
		.maximumSize(100)
		.build()

	fun getIp(request: HttpServletRequest): String {
		val ipAddress = request.getHeader("X-FORWARDED-FOR")
		return ipAddress?.let { it } ?: request.remoteAddr
	}

	fun loginSucceeded(key: String) {
		LOG.trace { "loginSucceeded [$key]" }
		attempts.invalidate(key)
	}

	fun loginFailed(key: String) {
		LOG.trace { "loginFailed [$key]" }
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
		LOG.trace { "isBlocked [$key]: $result" }
		return result
	}

	fun getAttempts() = attempts.asMap()
}
