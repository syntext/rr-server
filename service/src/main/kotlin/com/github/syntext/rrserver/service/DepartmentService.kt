package com.github.syntext.rrserver.service

import com.github.syntext.rrserver.model.Department
import com.github.syntext.rrserver.repository.DepartmentRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DepartmentService(private val departmentRepository: DepartmentRepository) {
	companion object {
		private val LOG = KotlinLogging.logger {}
	}

	@Transactional(readOnly = true)
	fun read(id: UUID) = departmentRepository.findByIdOrNull(id)

	@Transactional(readOnly = true)
	fun readByUserId(userId: UUID): Department = departmentRepository.findByUserId(userId)
}
