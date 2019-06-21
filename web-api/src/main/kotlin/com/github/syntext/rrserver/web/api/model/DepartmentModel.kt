package com.github.syntext.rrserver.web.api.model

import com.github.syntext.rrserver.model.Department
import org.springframework.beans.BeanUtils
import java.util.*

data class DepartmentModel(
	var id: UUID,
	var departmentName: String
) {
	constructor() : this(UUID.randomUUID(), "")

	constructor(source: Department?) : this() {
		source?.let {
			BeanUtils.copyProperties(source, this)
		}
	}
}
