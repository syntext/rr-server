package com.github.syntext.rrserver.web.console.configuration

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

private val log = KotlinLogging.logger {}

@Order(2)
@ControllerAdvice("com.github.syntext.rrserver.web.console")
class GlobalWebExceptionHandler {
	private val ERROR_BAD_REQUEST_VIEW = "/error/400"
	private val ERROR_UNAUTHORIZED_VIEW = "/error/401"
	private val ERROR_FORBIDDEN_VIEW = "/error/403"
	private val ERROR_NOT_FOUND_VIEW = "/error/404"
	private val ERROR_INTERNAL_SERVER_VIEW = "/error/500"

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(
		IllegalArgumentException::class,
		EmptyResultDataAccessException::class,
		DataIntegrityViolationException::class,
		BadRequestException::class,
		BindException::class
	)
	fun handleBadRequests(exception: Exception): String {
		log.warn(exception.javaClass.name + ": " + exception.message)
		return ERROR_BAD_REQUEST_VIEW
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(BadCredentialsException::class, UnauthorizedException::class)
	fun handleUnauthorized(exception: Exception): String {
		log.warn(exception.javaClass.name + ": " + exception.message)
		return ERROR_UNAUTHORIZED_VIEW
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException::class)
	fun handleForbidden(exception: Exception): String {
		log.warn(exception.javaClass.name + ": " + exception.message)
		return ERROR_FORBIDDEN_VIEW
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(PageNotFoundException::class)
	fun handleNotFound(exception: Exception): String {
		log.warn(exception.javaClass.name + ": " + exception.message)
		return ERROR_NOT_FOUND_VIEW
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception::class)
	fun handleInternalServerError(exception: Exception): String {
		log.warn(exception.javaClass.name + ": " + exception.message, exception)
		return ERROR_INTERNAL_SERVER_VIEW
	}
}
