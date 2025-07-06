package example.micronaut

import example.micronaut.config.GraphClient
import org.apache.tinkerpop.gremlin.process.traversal.Merge
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.slf4j.LoggerFactory
import kotlin.to

/**
 * ComposableGraphDbRepository provides a set of methods to interact with a Gremlin graph database.
 * It allows for creating, updating, and managing vertices and edges in the graph.
 *
 * This repository is designed to work with a Gremlin graph database client and provides methods
 * for saving vertices, creating edges, and handling properties in a composable manner.
 *
 * See on mergeV step in TinkerPop:
 * thinker pop
 * https://tinkerpop.apache.org/docs/3.6.2-SNAPSHOT/reference/#mergevertex-step
 * https://tinkerpop.apache.org/javadocs/3.6.2/core/org/apache/tinkerpop/gremlin/process/traversal/Merge.html
 * https://github.com/apache/tinkerpop/blob/master/docs/src/reference/the-traversal.asciidoc
 *
 * neptun
 * https://docs.aws.amazon.com/neptune/latest/userguide/gremlin-efficient-upserts.html
 *
 */
class ComposableGraphDbRepository(client: GraphClient) {
    private val g: GraphTraversalSource = client.g()
        ?: throw IllegalStateException("GraphTraversalSource is not initialized")
    private val gremlinCoreGraphdbRepository = GremlinCoreGraphdbRepository(client)


    val coreRepository: GremlinCoreGraphdbRepository
        get() = gremlinCoreGraphdbRepository

    val logger = LoggerFactory.getLogger(ComposableGraphDbRepository::class.java)


    /**
    ○  Save a vertex (create or update) based on the provided VertexArg.
    ○  If the vertex already exists, it updates the properties.
    ○  If it does not exist, it creates a new vertex with the specified properties.
     *
    ○  This methode do not work for example with Location with only lat and lon as identifyingProperties.
    ○  Location can exist many of them
     */
    fun saveVertex(vertexArg: VertexArg): GraphTraversal<Vertex, Vertex> {
        require(!vertexArg.label.isNullOrEmpty()) { "Vertex label cannot be null or empty" }

        // Properties split for mergeV logic
        val allProperties = vertexArg.properties ?: emptyMap()
        val identifyingProperties = vertexArg.identifyingProperties
        val nonIdentifyingProperties = allProperties.filterKeys { it !in identifyingProperties.keys }

        // Handle the case where both id and identifyingProperties are missing
        if (vertexArg.id.isNullOrEmpty() && identifyingProperties.isEmpty()) {
            return g.addV(vertexArg.label).apply {
                allProperties.forEach { (key, value) ->
                    this.property(key, value)
                }
            }
        }

        val searchCriteria = mutableMapOf<Any, Any>().apply {
            vertexArg.label.takeIf { it.isNotEmpty() }?.let { this[T.label] = it }
            if (!vertexArg.id.isNullOrEmpty()) {
                this[T.id] = vertexArg.id
            } else if (identifyingProperties.isNotEmpty()) {
                this.putAll(identifyingProperties)
            }
        }

        // Return the traversal pipeline with explicit generics in .option
        return g.mergeV(searchCriteria)
            .option<Any?, Map<Any?, Any?>>(
                Merge.onCreate,
                allProperties // Use all properties when creating a new vertex
            )
            .option<Any?, Map<Any?, Any?>>(
                Merge.onMatch,
                nonIdentifyingProperties // Update only non-identifying properties
            )
    }


    /**
    ○  Create multiple vertexes based on the provided VertexArg list.
    ○  Each VertexArg contains label and properties for the vertex.
     *
    ○  NB! Do not check if the vertex already exists, it will create a new vertex for each VertexArg.
     *
    ○  @param vertexArgs List of VertexArg objects representing the vertices to be created.
    ○  @return List of IDs of the created vertices.
     */
    fun createVertexes(vertexArgs: List<VertexArg>)
            : List<String> {
        val ids = mutableListOf<String>()
        vertexArgs.forEach { v ->
            val vertex = g.addV(v.label).apply {
                v.properties.forEach { (key, value) ->
                    this.property(key, value)
                }
            }.next()
            ids.add(vertex.id().toString())
        }
        return ids
    }

    fun createEdge(fromVertexId: String, toVertexId: String, edgeArg: EdgeArg) {
        val targetTraversal = GremlinHelper.v(toVertexId)
        val edgeTraversal = g.V(fromVertexId)
            .addE(edgeArg.label)
            .to(targetTraversal)
        edgeArg.edgeProps.forEach { (key, value) ->
            edgeTraversal.property(key, value)
        }
        edgeTraversal.iterate()
    }

    fun createEdgeIfNotExists(fromVertexId: String, toVertexId: String, edgeArg: EdgeArg) {
        val targetTraversal = GremlinHelper.v(toVertexId)
        val edgeTraversal = g.V(fromVertexId)
            .coalesce(
                GremlinHelper.outE(edgeArg.label)
                    .where(GremlinHelper.inV().hasId(toVertexId)),
                GremlinHelper.addE(edgeArg.label)
                    .to(targetTraversal)
            )
        edgeArg.edgeProps.forEach { (key, value) ->
            edgeTraversal.property(key, value)
        }
        edgeTraversal.iterate()
    }


    fun deduplicateProperties(propMap: Map<Any?, Any?>): Map<Any?, Any?> {
        // Ensure only unique key-value pairs exist in the property map
        val deduplicatedMap = mutableMapOf<Any?, Any?>()
        propMap.forEach { (key, value) ->
            if (!deduplicatedMap.containsKey(key) || deduplicatedMap[key] != value) {
                deduplicatedMap[key] = value
            }
        }
        return deduplicatedMap
    }


}

/**
•  Allows multiple identifying properties for matching vertices.
 */
data class VertexArg(
    val id: String? = null,
    val label: String? = null,
    val properties: Map<Any, Any> = emptyMap(), // Properties for the vertex
    val identifyingProperties: Map<Any, Any> = emptyMap() // Multiple identifying properties (replaces idProperty)
)

/**
•  Allows multiple identifying properties for matching edges.
 */
data class EdgeArg(
    val label: String,
    val edgeProps: Map<Any, Any> = emptyMap(), // Properties for the edge
    val identifyingProperties: Map<Any, Any> = emptyMap() // Multiple identifying properties
)