package com.github.syntext.rrserver.web.api.configuration.jwt

import com.github.syntext.rrserver.service.exception.UnauthorizedException
import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import com.github.syntext.rrserver.service.security.jwt.JwtTokenService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

class JwtTokenFilter(
	private val jwtTokenService: JwtTokenService,
	private val bruteForcePreventionService: BruteForcePreventionService
) : OncePerRequestFilter() {

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

	override fun doFilterInternal(
		httpServletRequest: HttpServletRequest,
		httpServletResponse: HttpServletResponse,
		filterChain: FilterChain
	) {
		val ip = bruteForcePreventionService.getIp(httpServletRequest)
		if (isBlocked(bruteForcePreventionService.getIp(httpServletRequest), httpServletRequest)) {
			SecurityContextHolder.clearContext()
			log.info("IP blocked: {}", ip)
			httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "IP blocked")
			return
		}

		val token = jwtTokenService.resolveToken(httpServletRequest)
		token?.let {
			try {
				if (token.isNotBlank() && jwtTokenService.validateToken(token)) {
					SecurityContextHolder.getContext().authentication = jwtTokenService.getAuthentication(token)
					bruteForcePreventionService.loginSucceeded(ip)
					filterChain.doFilter(httpServletRequest, httpServletResponse)
				}
			} catch (ue: UnauthorizedException) {
				SecurityContextHolder.clearContext()
				bruteForcePreventionService.loginFailed(ip)
				httpServletResponse.sendError(ue.getHttpStatus().value(), ue.message)
			}
		}
		return
	}
}

