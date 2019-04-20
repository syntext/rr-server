package com.github.syntext.rrserver.web.api.login.configuration

import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

class PayloadLoggingFilter : OncePerRequestFilter() {
	private val KEY_SESSION_ID = "sessionId"
	private val KEY_RESPONSE_TIME = "responseTime"

	@Throws(ServletException::class, IOException::class)
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		val requestWrapper = ContentCachingRequestWrapper(request)
		val responseWrapper = ContentCachingResponseWrapper(response)
		val startTime = System.currentTimeMillis()
		MDC.put(KEY_SESSION_ID, UUID.randomUUID().toString())

		try {
			filterChain.doFilter(requestWrapper, responseWrapper)
		} finally {
			val duration = (System.currentTimeMillis() - startTime).toString()
			MDC.put(KEY_RESPONSE_TIME, duration)
			log.info("Call duration {} ms for: {}", duration, createLogLine(requestWrapper, responseWrapper))
			MDC.clear()

			// Do not forget this line after reading response content or actual response will be empty!
			responseWrapper.copyBodyToResponse()
		}
	}

	private fun createLogLine(
		requestWrapper: ContentCachingRequestWrapper,
		responseWrapper: ContentCachingResponseWrapper
	): String {
		val line = StringBuilder()
		line.append(requestWrapper.requestURL.toString())

		val queryString = requestWrapper.queryString
		if (queryString.isNotBlank()) {
			line.append(":")
			line.append(queryString)
		}

		val requestBody = String(requestWrapper.contentAsByteArray)
		if (requestBody.isNotBlank()) {
			line.append(System.lineSeparator())
			line.append("REQUEST BODY: ")
			line.append(requestBody)
		}

		val responseBody = String(responseWrapper.contentAsByteArray)
		if (responseBody.isNotBlank()) {
			line.append(System.lineSeparator())
			line.append("RESPONSE BODY: ")
			line.append(responseBody)
		}

		return line.toString()
	}
}
