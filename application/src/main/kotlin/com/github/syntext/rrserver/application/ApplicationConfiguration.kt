package com.github.syntext.rrserver.application

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableAutoConfiguration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
@ComponentScan("com.github.syntext.rrserver")
@EnableJpaRepositories("com.github.syntext.rrserver.repository")
@EntityScan("com.github.syntext.rrserver.model")
class ApplicationConfiguration
