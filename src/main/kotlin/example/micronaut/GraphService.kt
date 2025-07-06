package example.micronaut

import example.micronaut.config.GraphClient
import jakarta.inject.Singleton
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.T

@Singleton
class GraphService(private val graphClient: GraphClient) {
    private val g : GraphTraversalSource = graphClient.g()
    private val graphdbRepository = ComposableGraphDbRepository(graphClient)

    fun countVertices(): Long {
        return g.V().hasLabel("Person").count().next()
    }

    fun addPerson(pnr: String, name: String, age: Int): Any {
        val vertexArg = VertexArg(
            label = "Person",
            properties = mapOf(
                "pnr" to pnr,
                "name" to name,
                "age" to age
            ),
            identifyingProperties = mapOf("pnr" to pnr
            )
        )
        return graphdbRepository.saveVertex(vertexArg).next().id()
    }

    fun findPersonByName(pnr: String): List<Any> {
        val data = g.V().has("pnr", pnr).valueMap<Any>().toList()
        return data
    }

    fun removeAll(){
        g.V().drop().iterate()
    }


}