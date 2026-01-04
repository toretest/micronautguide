package example.micronaut

import io.micronaut.context.annotation.Value
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import jakarta.inject.Singleton

@OpenAPIDefinition(
    info = Info(
            title = "micronautguide",
            version = "0.0"
    )
)

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val ctx = Micronaut.run(*args)
        val tore = ctx.getBean(Tore::class.java)
       tore.printName()

    }
}

@Singleton
data class Tore(@param:Value("\${toregard.name:not set}") private val name: String)
{
    fun printName() {
        println("************** Name: $name")
    }
}

