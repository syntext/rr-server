package com.github.syntext.rrserver.web.console.configuration.authentication

import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
class CustomAuthenticationSuccessHandler : SavedRequestAwareAuthenticationSuccessHandler() {

	@Autowired
	lateinit var bruteForcePreventionService: BruteForcePreventionService
	lateinit var failureUrl: String

	override fun onAuthenticationSuccess(
		request: HttpServletRequest,
		response: HttpServletResponse,
		authentication: Authentication
	) {
		val ip = bruteForcePreventionService.getIp(request)

		if (bruteForcePreventionService.isBlocked(ip)) {
			// Logout user
			request.logout()

			// Cleanup inlog info
			SecurityContextHolder.clearContext()
			val session = request.getSession(false)
			session?.invalidate()
			response.sendRedirect(failureUrl)

			return
		}

		bruteForcePreventionService.loginSucceeded(ip)
		super.onAuthenticationSuccess(request, response, authentication)
	}

	fun failureUrl(url: String) {
		this.failureUrl = url
	}
}
