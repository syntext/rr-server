package com.github.syntext.rrserver.web.api.service

import com.github.syntext.rrserver.service.DepartmentService
import com.github.syntext.rrserver.service.LocationService
import com.github.syntext.rrserver.service.UserService
import com.github.syntext.rrserver.web.api.model.DepartmentModel
import com.github.syntext.rrserver.web.api.model.LocationModel
import com.github.syntext.rrserver.web.api.model.UserModel
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserApiService(
	private val userService: UserService,
	private val departmentService: DepartmentService,
	private val locationService: LocationService
) {

	@GraphQLQuery(name = "user")
	fun read(): UserModel {
		val userId = UUID.fromString(getContext().authentication.name)
		return UserModel(userService.read(userId))
	}

	@GraphQLMutation(name = "save")
	fun save(@GraphQLArgument(name = "user") source: UserModel): UserModel {
		val userId = UUID.fromString(getContext().authentication.name)
		val destination = userService.read(userId)
			?: throw IllegalStateException("Illegal userId in authentication context")
		source.update(destination)
		userService.update(destination)
		return UserModel(destination)
	}

	@GraphQLQuery
	fun department(@GraphQLContext user: UserModel): DepartmentModel {
		return DepartmentModel(departmentService.readByUserId(user.id))
	}

	@GraphQLQuery
	fun location(@GraphQLContext department: DepartmentModel): LocationModel {
		return LocationModel(locationService.readByDepartmentId(department.id))
	}
}
