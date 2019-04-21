package com.github.syntext.rrserver.web.console.configuration

import com.github.syntext.rrserver.service.security.authentication.BruteForcePreventionService
import com.github.syntext.rrserver.web.console.configuration.authentication.CustomAuthenticationFailureHandler
import com.github.syntext.rrserver.web.console.configuration.authentication.CustomAuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import java.util.*
import javax.sql.DataSource

@Order(2)
@Configuration
class FormWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {
	companion object {
		private const val FAILURE_URL = "/login?error"
		private const val QUERY_USERNAME =
			"select ID, PASSWORD, case when DISABLED_ON is null then 'true' else 'false' end " + "as ENABLED from USERS where EMAIL=?"
		private const val QUERY_AUTHORITIES = "select USER_ID as ID, AUTHORITY from USER_ROLES where USER_ID=?::uuid"
	}

	@Autowired
	private lateinit var dataSource: DataSource

	@Autowired
	private lateinit var bruteForcePreventionService: BruteForcePreventionService

	@Autowired
	fun configureGlobal(auth: AuthenticationManagerBuilder) {
		auth.eraseCredentials(false)
			.jdbcAuthentication()
			.dataSource(dataSource)
			.passwordEncoder(passwordEncoder())
			.usersByUsernameQuery(QUERY_USERNAME)
			.authoritiesByUsernameQuery(QUERY_AUTHORITIES)
	}

	@Throws(Exception::class)
	override fun configure(http: HttpSecurity) {
		// Allow iFrames within same origin
		http.headers().frameOptions().disable()

		//@formatter:off
		http
			.authorizeRequests()
			.antMatchers("/console/**").hasAnyRole("ADMINISTRATOR")
			.anyRequest().permitAll()

			//Configures form login
			.and()
			.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/")
			.failureUrl(FAILURE_URL)
			.successHandler(authenticationSuccessHandler())
			.failureHandler(authenticationFailureHandler())

			//Configures the logout function
			.and()
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.deleteCookies("JSESSIONID")
		//@formatter:on
	}

	@Bean
	fun authenticationSuccessHandler(): CustomAuthenticationSuccessHandler {
		return CustomAuthenticationSuccessHandler(bruteForcePreventionService, FAILURE_URL)
	}

	@Bean
	fun authenticationFailureHandler(): CustomAuthenticationFailureHandler {
		return CustomAuthenticationFailureHandler(bruteForcePreventionService, FAILURE_URL)
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		val idForEncode = "bcrypt"
		val defaultEncoder = BCryptPasswordEncoder()

		val encoders = HashMap<String, PasswordEncoder>()
		encoders[idForEncode] = defaultEncoder

		return DelegatingPasswordEncoder(idForEncode, encoders)
	}

	@Bean
	fun authenticationEventPublisher(publisher: ApplicationEventPublisher): AuthenticationEventPublisher {
		return DefaultAuthenticationEventPublisher(publisher)
	}

	@Bean
	fun persistentTokenRepository(): PersistentTokenRepository {
		val db = JdbcTokenRepositoryImpl()
		db.setDataSource(dataSource)
		return db
	}
}
