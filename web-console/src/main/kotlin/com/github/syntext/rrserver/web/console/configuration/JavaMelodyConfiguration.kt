package com.github.syntext.rrserver.web.console.configuration

import net.bull.javamelody.MonitoringFilter
import net.bull.javamelody.MonitoringSpringAdvisor
import net.bull.javamelody.Parameter
import net.bull.javamelody.SpringDataSourceBeanPostProcessor
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.servlet.DispatcherType

@Configuration
class JavaMelodyConfiguration {
	/*TODO: when import quartz
	<bean id="quartzScheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
		<property name="exposeSchedulerInRepository" value="true" />
	</bean>
	*/

	@Bean
	fun javaMelody(): FilterRegistrationBean<MonitoringFilter> {
		val javaMelody = FilterRegistrationBean<MonitoringFilter>()
		javaMelody.run {
			filter = MonitoringFilter()
			setName("javamelody")
			setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC)
			addInitParameter(Parameter.LOG.code, java.lang.Boolean.toString(true))
			addInitParameter(
				Parameter.QUARTZ_DEFAULT_LISTENER_DISABLED.code,
				java.lang.Boolean.toString(true)
			)
			addInitParameter(Parameter.MONITORING_PATH.code, "/console/javamelody")
			addUrlPatterns("/*")
		}
		return javaMelody
	}

	@Bean
	fun monitoringDataSourceBeanPostProcessor(): SpringDataSourceBeanPostProcessor {
		val processor = SpringDataSourceBeanPostProcessor()
		processor.setExcludedDatasources(null)
		return processor
	}

	@Bean
	fun springServiceMonitoringAdvisor(): MonitoringSpringAdvisor {
		val interceptor = MonitoringSpringAdvisor()
		interceptor.setPointcut(AnnotationMatchingPointcut(Component::class.java))
		return interceptor
	}
}
