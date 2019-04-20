package com.github.syntext.rrserver.web.console.configuration.authentication

import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFailureHandler(defaultFailureUrl: String) :
	SimpleUrlAuthenticationFailureHandler(defaultFailureUrl) {

	@Autowired
	lateinit var bruteForcePreventionService: BruteForcePreventionService

	override fun onAuthenticationFailure(
		request: HttpServletRequest,
		response: HttpServletResponse,
		exception: AuthenticationException
	) {
		bruteForcePreventionService.loginFailed(bruteForcePreventionService.getIp(request))
		super.onAuthenticationFailure(request, response, exception)
	}
}
