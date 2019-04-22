package com.github.syntext.rrserver.web.api

import graphql.ExecutionInput
import graphql.GraphQL
import graphql.Internal
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import io.leangen.graphql.GraphQLSchemaGenerator
import io.leangen.graphql.metadata.strategy.query.AnnotatedResolverBuilder
import io.leangen.graphql.metadata.strategy.value.jackson.JacksonValueMapperFactory
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

@Validated
@Internal
abstract class AbstractGraphQLController(vararg services: Any) {

	private val schema: GraphQLSchema = GraphQLSchemaGenerator()
		.withResolverBuilders(AnnotatedResolverBuilder())
		.withOperationsFromSingletons(*services)
		.withValueMapperFactory(JacksonValueMapperFactory())
		.generate()
	private val graphQL: GraphQL = GraphQL.newGraphQL(this.schema).build()

	@GetMapping(produces = [MediaType.APPLICATION_JSON_UTF8_VALUE], params = ["schema"])
	fun schema(): String {
		return SchemaPrinter(
			SchemaPrinter.Options.defaultOptions()
				.includeScalarTypes(true)
				.includeSchemaDefintion(true)
		).print(this.schema)
	}

	@PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun query(@Valid @RequestBody body: GraphQLRequestBody): Map<String, Any> {
		val executionInput = ExecutionInput.newExecutionInput()
			.query(body.query)
			.operationName(body.operationName)
			.variables(body.variables)
			.build()
		return graphQL.execute(executionInput).toSpecification()
	}
}
