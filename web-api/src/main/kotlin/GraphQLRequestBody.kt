package com.github.syntext.rrserver.web.api

data class GraphQLRequestBody(
	var query: String,
	var operationName: String?,
	var variables: Map<String, Any>?
)
