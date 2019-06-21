package com.github.syntext.rrserver.repository

import com.github.syntext.rrserver.model.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface LocationRepository : JpaRepository<Location, UUID> {

	@Query(
		nativeQuery = true,
		value = "SELECT * FROM locations l WHERE l.id = (SELECT d.location_id FROM departments d WHERE d.id=:departmentId)"
	)
	fun findByDepartmentId(@Param("departmentId") departmentId: UUID): Location
}
