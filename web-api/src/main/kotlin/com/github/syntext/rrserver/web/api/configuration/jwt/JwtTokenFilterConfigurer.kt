package com.github.syntext.rrserver.web.api.configuration.jwt

import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import com.github.syntext.rrserver.service.security.jwt.JwtTokenService
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtTokenFilterConfigurer(
	val jwtTokenService: JwtTokenService,
	val bruteForcePreventionService: BruteForcePreventionService
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

	override fun configure(http: HttpSecurity) {
		val customFilter = JwtTokenFilter(
			jwtTokenService,
			bruteForcePreventionService
		)
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter::class.java)
	}
}
