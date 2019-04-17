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
	val id: UUID = UUID.randomUUID(),
	@Column(nullable = false)
	val firstName: String = "",
	val infix: String? = null,
	@Column(nullable = false)
	val lastName: String = "",
	@Column(nullable = false)
	val password: String? = null,
	@Column(nullable = false)
	val email: String? = null,
	val languageCode: Locale = Locale.ENGLISH,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	val dateOfBirth: LocalDate? = null,

	@Enumerated(value = STRING)
	val gender: GenderType = GenderType.FEMALE,

	@Column(insertable = false, updatable = false)
	val createdOn: ZonedDateTime = ZonedDateTime.now(),
	@Column(insertable = false)
	val lastModified: ZonedDateTime = ZonedDateTime.now(),
	val disabledOn: ZonedDateTime? = null,

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "MEMBER_ROLES", joinColumns = [JoinColumn(name = "MEMBER_ID")])
	@Column(name = "AUTHORITY")
	@Enumerated(STRING)
	val roles: Set<UserRoleType> = HashSet()
)
