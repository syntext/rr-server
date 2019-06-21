package com.github.syntext.rrserver.model

import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "LOCATIONS")
data class Location(
	@Id
	var id: UUID,
	@Column(nullable = false)
	var locationName: String,

	@Column(insertable = false, updatable = false)
	var createdOn: ZonedDateTime,
	@Column(insertable = false)
	var lastModified: ZonedDateTime,
	var disabledOn: ZonedDateTime?
)
