package com.github.syntext.rrserver.service.exception

import org.springframework.http.HttpStatus

class UnauthorizedException(message: String) : RuntimeException(message) {
	fun getHttpStatus() = HttpStatus.UNAUTHORIZED
}
