package com.github.syntext.rrserver.web.api.model

import com.github.syntext.rrserver.model.Department
import com.github.syntext.rrserver.model.Location
import org.springframework.beans.BeanUtils
import java.util.*

data class LocationModel(
	var id: UUID,
	var locationName: String
) {
	constructor() : this(UUID.randomUUID(), "")

	constructor(source: Location?) : this() {
		source?.let {
			BeanUtils.copyProperties(source, this)
		}
	}
}
