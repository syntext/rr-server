package com.github.syntext.rrserver.application

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableAutoConfiguration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
class ApplicationConfiguration {
}
