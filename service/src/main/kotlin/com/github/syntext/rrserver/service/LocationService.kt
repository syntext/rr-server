package com.github.syntext.rrserver.service

import com.github.syntext.rrserver.model.Department
import com.github.syntext.rrserver.model.Location
import com.github.syntext.rrserver.repository.DepartmentRepository
import com.github.syntext.rrserver.repository.LocationRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class LocationService(private val locationRepository: LocationRepository) {
	companion object {
		private val LOG = KotlinLogging.logger {}
	}

	@Transactional(readOnly = true)
	fun read(id: UUID) = locationRepository.findByIdOrNull(id)

	@Transactional(readOnly = true)
	fun readByDepartmentId(departmentId: UUID): Location = locationRepository.findByDepartmentId(departmentId)
}
