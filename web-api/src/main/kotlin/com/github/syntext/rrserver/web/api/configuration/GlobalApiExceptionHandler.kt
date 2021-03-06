package com.github.syntext.rrserver.web.api.configuration

import com.github.syntext.rrserver.service.exception.BadRequestException
import com.github.syntext.rrserver.service.exception.ForbiddenException
import com.github.syntext.rrserver.service.exception.PageNotFoundException
import com.github.syntext.rrserver.service.exception.UnauthorizedException
import mu.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@Order(2)
@ControllerAdvice("com.github.syntext.rrserver.web.api")
class GlobalApiExceptionHandler {
	companion object {
		private val LOG = KotlinLogging.logger {}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(
		IllegalArgumentException::class,
		EmptyResultDataAccessException::class,
		DataIntegrityViolationException::class,
		BadRequestException::class,
		BindException::class
	)
	fun handleBadRequests(exception: Exception) {
		LOG.warn(exception.javaClass.name + ": " + exception.message)
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(BadCredentialsException::class, UnauthorizedException::class)
	fun handleUnauthorized(exception: Exception) {
		LOG.warn(exception.javaClass.name + ": " + exception.message)
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException::class)
	fun handleForbidden(exception: Exception) {
		LOG.warn(exception.javaClass.name + ": " + exception.message)
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(PageNotFoundException::class)
	fun handleNotFound(exception: Exception) {
		LOG.warn(exception.javaClass.name + ": " + exception.message)
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception::class)
	fun handleInternalServerError(exception: Exception) {
		LOG.warn(exception.javaClass.name + ": " + exception.message, exception)
	}
}
