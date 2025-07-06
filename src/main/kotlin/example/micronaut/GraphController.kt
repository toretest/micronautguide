package example.micronaut

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Post

@Controller("/graph")
class GraphController(private val graphService: GraphService) {

    @Get("/vertices/count")
    fun getVertexCount(): HttpResponse<Long> {
        val count = graphService.countVertices()
        return HttpResponse.ok(count)
    }

    @Post("/vertices/post/person")
    fun postVertexPerson(person : Person): HttpResponse<Any> {
        val count = graphService.addPerson(name = person.name, age = person.age,  pnr = person.pnr)
        return HttpResponse.ok(count)
    }
}

