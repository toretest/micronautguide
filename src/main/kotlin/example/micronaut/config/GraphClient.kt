package example.micronaut.config

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Graph

interface GraphClient {
    fun graph(): Graph
    fun g(): GraphTraversalSource
}