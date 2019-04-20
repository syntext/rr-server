package com.github.syntext.rrserver.web.api.configuration

import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import com.github.syntext.rrserver.service.security.jwt.JwtTokenService
import com.github.syntext.rrserver.web.api.configuration.jwt.JwtTokenFilterConfigurer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain

@Order(1)
@Configuration
class ApiWebSecurityConfigurationAdapter : WebSecurityConfigurerAdapter() {

	@Autowired
	lateinit var jwtTokenProvider: JwtTokenService
	@Autowired
	lateinit var bruteForcePreventionService: BruteForcePreventionService

	override fun configure(http: HttpSecurity) {
		//@formatter:off
		// Disable CSRF (cross site request forgery)
		http.csrf().disable()

		// Allow CORS
		http.cors()

		// No session will be created or used by spring security
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

		// Entry points
		http
			.antMatcher("/api/**")
			.authorizeRequests()
			.antMatchers("/api/login/**").permitAll()
			.antMatchers("/api/**").authenticated()

		// Apply JWT
		http.apply<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>>(
			JwtTokenFilterConfigurer(
				jwtTokenProvider,
				bruteForcePreventionService
			)
		)
		//@formatter:on
	}

	@Bean
	fun payloadLoggingFilter(): FilterRegistrationBean<PayloadLoggingFilter> {
		val registrationBean = FilterRegistrationBean<PayloadLoggingFilter>()
		registrationBean.filter = PayloadLoggingFilter()
		registrationBean.addUrlPatterns("/api/*")
		return registrationBean
	}
}
