package com.github.syntext.rrserver.model

import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.FetchType.EAGER

@Entity
@Table(name = "DEPARTMENTS")
data class Department(
	@Id
	var id: UUID,
	@Column(nullable = false)
	var departmentName: String,

	@Column(insertable = false, updatable = false)
	var createdOn: ZonedDateTime,
	@Column(insertable = false)
	var lastModified: ZonedDateTime,
	var disabledOn: ZonedDateTime?,

	@ManyToOne(optional = false, fetch = EAGER)
	@JoinColumn(name = "LOCATION_ID")
	var location: Location
)
