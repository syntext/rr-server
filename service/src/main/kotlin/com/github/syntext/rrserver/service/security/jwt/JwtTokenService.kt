package com.github.syntext.rrserver.service.security.jwt

import com.github.syntext.rrserver.enumeration.UserRoleType
import com.github.syntext.rrserver.model.User
import com.github.syntext.rrserver.repository.UserRepository
import com.github.syntext.rrserver.service.exception.UnauthorizedException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList


@Service
class JwtTokenService(val userRepository: UserRepository,
					  val passwordEncoder: PasswordEncoder,
					  @Value("#{jwt.secret.key}") val secretKeyString: String) {


	private val CLAIM_KEY: String = "auth"
	private val HTTP_AUTHENTICATION_HEADER: String = "Authorization"
	private val HTTP_AUTHENTICATION_SCHEME: String = "Bearer "
	private val HTTP_AUTHENTICATION_SCHEME_LENGTH: Int = HTTP_AUTHENTICATION_SCHEME.length
	private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())

	fun doRefresh(): String? {
		// TODO: implement me!
		return null
	}

	fun doLogin(email: String, password: String): String {
		val user = userRepository.findByEmail(email) ?: throw UnauthorizedException("User email not found.")

		if (!passwordEncoder.matches(password, user.password)) {
			throw UnauthorizedException("Invalid login. Please check your name and password.")
		}

		try {
			SecurityContextHolder.getContext().authentication = getAuthentication(user)
			return createToken(user.id.toString(), ArrayList(user.roles))
		} catch (ae: AuthenticationException) {
			throw UnauthorizedException("Invalid username/password supplied")
		}
	}

	fun getAuthentication(username: UUID): Authentication {
		return getAuthentication(userRepository.findByIdOrNull(username))
	}

	fun getAuthentication(token: String): Authentication {
		val username = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
		return getAuthentication(UUID.fromString(username))
	}

	fun getAuthentication(user: User?): Authentication {
		if (user == null) {
			throw UnauthorizedException("Member not found!")
		}
		return UsernamePasswordAuthenticationToken(user.id, "", user.roles)
	}

	fun resolveToken(req: HttpServletRequest): String? {
		val bearerToken = req.getHeader(HTTP_AUTHENTICATION_HEADER)
		return if (bearerToken.isNotBlank() && bearerToken.startsWith(HTTP_AUTHENTICATION_SCHEME)) {
			bearerToken.substring(HTTP_AUTHENTICATION_SCHEME_LENGTH)
		} else null
	}

	fun validateToken(token: String): Boolean {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
			return true
		} catch (e: JwtException) {
			throw UnauthorizedException("Expired or invalid JWT token")
		} catch (e: IllegalArgumentException) {
			throw UnauthorizedException("Expired or invalid JWT token")
		}

	}

	// --[ LOGIC ]------------------------------------------------------------------------------------------------------
	private fun createToken(username: String, roles: List<UserRoleType>): String {

		val roleList = mutableListOf<String>()
		for (role in roles) {
			roleList.add(role.authority)
		}

		val claims = Jwts.claims()
		claims.subject = username
		claims[CLAIM_KEY] = roleList.joinToString()

		val calendar = Calendar.getInstance()
		val issued = calendar.time
		calendar.add(Calendar.MONTH, 1)
		val expiration = calendar.time

		return Jwts.builder()
			.setId(UUID.randomUUID().toString())
			.setIssuedAt(issued)
			.setExpiration(expiration)
			.setClaims(claims)
			.signWith(secretKey)
			.compact()
	}
}
