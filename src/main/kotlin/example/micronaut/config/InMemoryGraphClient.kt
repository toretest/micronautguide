package example.micronaut.config

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

@Singleton
@Requires(property = "gremlin.mode", value = "mem")
class InMemoryGraphClient : GraphClient {
    private val graph: Graph = TinkerGraph.open()
    private val g: GraphTraversalSource = graph.traversal()

    override fun getGraph(): Graph = graph
    override fun g(): GraphTraversalSource = g
}