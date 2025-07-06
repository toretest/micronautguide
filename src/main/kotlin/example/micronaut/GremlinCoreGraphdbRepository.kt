package example.micronaut

import example.micronaut.config.GraphClient
import jakarta.inject.Singleton
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Edge
import kotlin.collections.iterator

/**
•  Generic repository for Gremlin Graph Database operations.
 *
•   low-level, generic repository for basic CRUD operations
 */
@Singleton
class GremlinCoreGraphdbRepository(
    client: GraphClient
) {
    val g: GraphTraversalSource = client.g()
        ?: throw IllegalStateException("GraphTraversalSource is not initialized")

    private val logger = org.slf4j.LoggerFactory.getLogger(GremlinCoreGraphdbRepository::class.java)

    fun vertexExists(label: String, properties: Map<Any, Any>): Boolean {
        var traversal = g.V().hasLabel(label)
        for ((key, value) in properties) {
            traversal = traversal.has(key.toString(), value)
        }
        return traversal.tryNext().isPresent
    }

    /**
    ○  Checks if a vertex exists with the specified label and properties.
    ○  Returns a pair containing a boolean indicating existence and the vertex if found.
     *
    ○  @param label The label of the vertex to check.
    ○  @param properties A map of properties to match against the vertex.
    ○  @return A pair containing a boolean indicating existence and the vertex if found.
     */
    fun vertexExistsWithReturn(label: String, properties: Map<Any, Any>): Pair<Boolean, Vertex?> {
        var traversal = g.V().hasLabel(label)
        for ((key, value) in properties) {
            traversal = traversal.has(key.toString(), value)
        }

        val result = traversal.tryNext()
        return Pair(result.isPresent, result.orElse(null))
    }

    fun createVertex(label: String, properties: Map<Any, Any>): Vertex {
        var traversal = g.addV(label)
        properties.forEach { (key, value) ->
            traversal = traversal.property(key, value)
        }
        return traversal.next()
    }

    fun readVertexById(id: Any): Vertex? {
        return g.V(id).tryNext().orElse(null)
    }

    fun updateVertexProperty(id: Any, key: String, value: Any): Vertex? {
        return g.V(id).property(key, value).next()
    }

    fun deleteVertex(id: Any) {
        g.V(id).drop().iterate()
    }

    fun deleteEdge(id: Any) {
        g.E(id).drop().iterate()
    }

    fun deleteVertexByLabel(label: String) {
        g.V().hasLabel(label).drop().iterate()
    }

    /**
    ○  Deletes all vertices with the specified root ID.
    ○  This method is a placeholder and should be implemented based on your specific requirements.
     *
    ○  @param id The root ID of the vertices to delete.
    ○  g.V().hasLabel("Route").drop().iterate()
    ○  g.V().hasLabel("Location").drop().iterate()
     */
    fun deleteAllVerticesWithRootId(label: String, id: Any) {
        g.V(id).hasLabel(label).drop().iterate()
    }

    fun dropGraph() {
        g.V().drop().iterate()
        g.E().drop().iterate()
    }


    fun createEdge(fromId: Any, toId: Any, label: String, properties: Map<String, Any>): Edge {
        var traversal = g.V(fromId).`as`("a")
            .V(toId).addE(label).from("a")

        properties.forEach { (key, value) ->
            traversal = traversal.property(key, value)
        }

        return traversal.next()
    }

    fun readEdgeById(id: Any): Edge? {
        return g.E(id).tryNext().orElse(null)
    }

    fun updateEdgeProperty(id: Any, key: String, value: Any): Edge? {
        return g.E(id).property(key, value).next()
    }

    fun updateEdgeProperties(id: Any, properties: Map<Any, Any>): Edge? {
        var traversal = g.E(id)
        properties.forEach { (key, value) ->
            traversal = traversal.property(key, value)
        }
        return traversal.tryNext().orElse(null)
    }

    fun removePropertiesById(vertexId: String, propertiesToRemove: List<String>): Boolean {
        require(vertexId.isNotBlank()) { "Vertex ID cannot be blank" }
        require(propertiesToRemove.isNotEmpty()) { "No properties specified to remove" }
        val traversal = g.V(vertexId)
        if (!traversal.hasNext()) {
            logger.warn("No vertex found with ID: $vertexId")
            return false
        }
        val vertex = traversal.next()
        propertiesToRemove.forEach { key ->
            vertex.properties<Any>(key).forEachRemaining { it.remove() }
        }
        return true
    }

    fun removePropertiesByMatch(
        label: String,
        identifyingProperties: Map<String, Any>,
        propertiesToRemove: List<String>
    ): Boolean {
        require(label.isNotBlank()) { "Label cannot be blank" }
        require(identifyingProperties.isNotEmpty()) { "Identifying properties are required" }
        require(propertiesToRemove.isNotEmpty()) { "No properties specified to remove" }
        var traversal = g.V().hasLabel(label)
        identifyingProperties.forEach { (k, v) ->
            traversal = traversal.has(k, v)
        }
        if (!traversal.hasNext()) {
            logger.warn("No vertex matched label=$label with props=$identifyingProperties")
            return false
        }

        val vertex = traversal.next()
        propertiesToRemove.forEach { key ->
            vertex.properties<Any>(key).forEachRemaining { it.remove() }
        }
        return true
    }


}