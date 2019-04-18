package com.github.syntext.rrserver.web.api.login

import com.github.syntext.rrserver.service.exception.BadRequestException
import com.github.syntext.rrserver.service.exception.UnauthorizedException
import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import com.github.syntext.rrserver.service.security.jwt.JwtTokenService
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RequestMapping("/api/login")
@RestController
class LoginApiController(val jwtTokenService: JwtTokenService,
						 val bruteForcePreventionService: BruteForcePreventionService) {

	private val log = LoggerFactory.getLogger(LoginApiController::class.java)

	@PostMapping
	fun login(@RequestBody model: AuthenticationModel,
			  request: HttpServletRequest): TokenModel {

		val ip = bruteForcePreventionService.getIp(request)
		if (isBlocked(bruteForcePreventionService.getIp(request), request)) {
			SecurityContextHolder.clearContext()
			log.info("IP blocked: {}", ip)
			throw UnauthorizedException("IP blocked: $ip")
		}

		if (model.username.isBlank() || model.password.isBlank()) {
			throw BadRequestException("Please fill in username and password")
		}

		try {
			val result = TokenModel(jwtTokenService.doLogin(model.username, model.password))
			bruteForcePreventionService.loginSucceeded(ip)
			return result
		} catch (ue: UnauthorizedException) {
			bruteForcePreventionService.loginFailed(ip)
			throw ue
		}
	}

	@GetMapping("/refresh")
	fun refresh(request: HttpServletRequest): TokenModel? {
		return null
	}

	private fun isBlocked(ip: String, request: HttpServletRequest): Boolean {
		if (bruteForcePreventionService.isBlocked(ip)) {
			// Logout user
			request.logout()

			// Cleanup inlog info
			SecurityContextHolder.clearContext()
			val session = request.getSession(false)
			session?.invalidate()
			return true
		}
		return false
	}
}
