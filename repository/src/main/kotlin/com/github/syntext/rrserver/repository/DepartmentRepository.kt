package com.github.syntext.rrserver.repository

import com.github.syntext.rrserver.model.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface DepartmentRepository : JpaRepository<Department, UUID> {

	@Query(
		nativeQuery = true,
		value = "SELECT * FROM departments d WHERE d.id = (SELECT u.department_id FROM users u WHERE u.id=:userId)"
	)
	fun findByUserId(@Param("userId") userId: UUID): Department
}
