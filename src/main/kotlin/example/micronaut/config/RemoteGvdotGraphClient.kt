package example.micronaut.config

import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.util.ser.GraphSONMessageSerializerV3

@Singleton
@Requires(property = "gremlin.mode", value = "gvdot")
class RemoteGvdotGraphClient(
    @Value("\${gremlin.endpoint}") private val endpoint: String
) : GraphClient {

    private val g: GraphTraversalSource

    private val cluster = Cluster.build(endpoint)
        //.addContactPoint(endpoint)
        .port(9182)
        //.enableSsl(false)
        //.serializer(GraphSONMessageSerializerV1())
        .serializer(GraphSONMessageSerializerV3())
        .create()

    init {
        this.g = AnonymousTraversalSource.traversal()
            .withRemote(DriverRemoteConnection.using(cluster, "g"))
    }

    override fun g(): GraphTraversalSource = g

    override fun graph(): Graph {
        throw UnsupportedOperationException("Remote graph does not support direct Graph access")
    }
}