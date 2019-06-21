package com.github.syntext.rrserver.model

import com.github.syntext.rrserver.enumeration.GenderType
import com.github.syntext.rrserver.enumeration.UserRoleType
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.persistence.FetchType.EAGER

@Entity
@Table(name = "USERS")
data class User(
	@Id
	var id: UUID,
	@Column(nullable = false)
	var firstName: String,
	var infix: String? = null,
	@Column(nullable = false)
	var lastName: String,
	@Column(nullable = false)
	var password: String,
	@Column(nullable = false)
	var email: String,
	var languageCode: Locale,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	var dateOfBirth: LocalDate?,

	@Enumerated(value = STRING)
	var gender: GenderType,

	@Column(insertable = false, updatable = false)
	var createdOn: ZonedDateTime,
	@Column(insertable = false)
	var lastModified: ZonedDateTime,
	var disabledOn: ZonedDateTime?,

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "USER_ROLES", joinColumns = [JoinColumn(name = "USER_ID")])
	@Column(name = "AUTHORITY")
	@Enumerated(STRING)
	var roles: MutableSet<UserRoleType>,

	@ManyToOne(optional = false, fetch = EAGER)
	@JoinColumn(name="DEPARTMENT_ID")
	var department: Department
)
