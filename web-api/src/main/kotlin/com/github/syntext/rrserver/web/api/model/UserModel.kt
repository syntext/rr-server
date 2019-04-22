package com.github.syntext.rrserver.web.api.model

import com.github.syntext.rrserver.enumeration.GenderType
import com.github.syntext.rrserver.model.User
import org.springframework.beans.BeanUtils
import java.time.LocalDate

data class UserModel(
	var firstName: String,
	var infix: String?,
	var lastName: String,
	var email: String,
	var dateOfBirth: LocalDate?,
	var gender: GenderType?
) {
	constructor() : this("", null, "", "", null, null)

	constructor(source: User?): this() {
		source?.let {
			BeanUtils.copyProperties(source, this)
		}
	}

	fun update(destination: User) {
		if (firstName.isNotBlank()) {
			destination.firstName = firstName
		}
		infix?.let {
			destination.infix = infix
		}
		if (lastName.isNotBlank()) {
			destination.lastName = lastName
		}
		if (email.isNotBlank()) {
			destination.email = email
		}
	}
}
