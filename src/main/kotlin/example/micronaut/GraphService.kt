package example.micronaut

import example.micronaut.config.GraphClient
import jakarta.inject.Singleton

@Singleton
class GraphService(private val graphClient: GraphClient) {
    private val g = graphClient.g()

    fun countVertices(): Long {
        return g.V().count().next()
    }

    fun addPerson( name : String, age : String) : Any {

        val dd = g.V().addV("Person")
            .property("name", name)
            .property("age", age) // or a number type
            .iterate()
        println(dd)

        return "dd"
    }

    fun findPersonByName(name: String): List<Any> {
        val data = g.V().has("name", name).valueMap<Any>().toList()
        return data
    }

    fun removeAll(){
        g.V().drop().iterate()
    }


}