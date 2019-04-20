package com.github.syntext.rrserver.web.api.com.github.syntext.rrserver.web.api.service

import com.github.syntext.rrserver.service.UserService
import com.github.syntext.rrserver.web.api.com.github.syntext.rrserver.web.api.model.UserModel
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserApiService(val userService: UserService) {

	@GraphQLQuery(name = "member")
	fun read(): UserModel {
		val memberId = UUID.fromString(getContext().getAuthentication().getName())
		return UserModel(userService.read(memberId))
	}

	@GraphQLMutation(name = "save")
	fun save(@GraphQLArgument(name = "member") source: UserModel): UserModel {
		val memberId = UUID.fromString(getContext().authentication.name)
		val destination = userService.read(memberId)
		destination?.let {
			source.update(destination)
			userService.update(destination)
		}
		return UserModel(destination)
	}
}
