package example.micronaut

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpStatus
import io.micronaut.serde.annotation.Serdeable

@Controller("/micronautguide")
class MicronautguideController {

    @Get(uri="/", produces=["applicaton/json"])
    fun index(): Hello {
        return Hello("hello")
    }
}

@Serdeable
data class Hello(val message: String)