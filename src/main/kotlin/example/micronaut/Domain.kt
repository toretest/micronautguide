package example.micronaut

import io.micronaut.serde.annotation.Serdeable


@Serdeable
data class Person(val name: String, val age: Int, val pnr : String)