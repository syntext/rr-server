package com.github.syntext.rrserver.web.console.configuration

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.Locale.ENGLISH

@EnableWebSecurity
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableCaching
@ServletComponentScan("com.github.syntext.rrserver.web.console")
@EnableAutoConfiguration
@Configuration
class ConsoleConfiguration : WebMvcConfigurer {
	private val DEFAULT_ENCODING = "UTF-8"

	// ==[ INTERNATIONALIZATION ]=======================================================================================
	@Bean
	fun messageSource(): ReloadableResourceBundleMessageSource {
		val messageSource = ReloadableResourceBundleMessageSource()
		messageSource.setBasename("classpath:/languages/messages")
		messageSource.setDefaultEncoding(DEFAULT_ENCODING)
		return messageSource
	}

	@Bean
	fun mailSource(): ReloadableResourceBundleMessageSource {
		val messageSource = ReloadableResourceBundleMessageSource()
		messageSource.setBasename("classpath:/languages/mail")
		messageSource.setDefaultEncoding(DEFAULT_ENCODING)
		return messageSource
	}

	@Bean
	fun localeResolver(): LocaleResolver {
		val resolver = AcceptHeaderLocaleResolver()
		resolver.defaultLocale = ENGLISH
		return resolver
	}

	// ==[ RESOURCES ]==================================================================================================
	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler("/assets/**")
			.addResourceLocations("classpath:/assets/")
			.setCachePeriod(3600)
	}
}
