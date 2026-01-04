package example.micronaut

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
            title = "micronautguide",
            version = "0.0"
    )
)
object Application

fun main(args: Array<String>) {
	run(*args)
}

