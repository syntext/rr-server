package com.github.syntext.rrserver.web.api

import com.github.syntext.rrserver.web.api.service.UserApiService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class ApiGraphQLController(userApiService: UserApiService) : AbstractGraphQLController(userApiService)
