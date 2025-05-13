# doc

##

http://localhost:8080/swagger-ui#/default/addLocation

https://github.com/aws/neptune-gremlin-client

## Janusgraph docker

https://hub.docker.com/r/janusgraph/janusgraph

docker run --name janusgraph-default janusgraph/janusgraph:latest
docker run --name janusgraph-default -p 8182:8182 janusgraph/janusgraph:latest
docker run -d --name janusgraph-default -p 8182:8182 janusgraph/janusgraph:latest
docker logs -f janusgraph-default


docker run --rm --link janusgraph-default:janusgraph -e GREMLIN_REMOTE_HOSTS=janusgraph \
-it docker.io/janusgraph/janusgraph:latest ./bin/gremlin.sh

https://github.com/JanusGraph/janusgraph-docker?tab=readme-ov-file

## This is the client-side driver to communicate with a remote Gremlin Server (e.g. JanusGraph, Neptune, or any TinkerPop-enabled server).

```xml
<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>gremlin-driver</artifactId>
    <version>3.7.3</version>
</dependency>
```

```gradle

implementation("org.apache.tinkerpop:gremlin-driver:3.7.3")
```

```kotlin
val cluster = Cluster.build("localhost").port(8182).create()
val client = cluster.connect()
val result = client.submit("g.V().count()")

```

```kotlin
 private val cluster: Cluster = Cluster.build()
        .addContactPoint(endpoint)
        .port(8182)
        .enableSsl(false) // Ensure SSL is correctly set
        .serializer(GraphSONMessageSerializerV3()) // Switch to GraphSON
        .create()

    private val client: Client = cluster.connect()

    fun g() = AnonymousTraversalSource.traversal()
        .withRemote(DriverRemoteConnection.using(cluster, "g"))

    fun close() {
        client.close()
        cluster.close()
    }

g().V().hasLabel("Person").drop().iterate()
println("Deleted all Person vertices.")

```

# in-memory graph database
This is an in-memory graph database implementation that is embedded and non-persistent.
•	It’s perfect for testing, prototyping, and small graph workloads without needing a server.
•	Provides a full implementation of the TinkerPop Graph interface.

```xml
<dependency>
    <groupId>org.apache.tinkerpop</groupId>
    <artifactId>tinkergraph-gremlin</artifactId>
    <version>3.7.3</version>
</dependency>
```
or 
```gradle
implementation("org.apache.tinkerpop:tinkergraph-gremlin:3.7.3")
```


```kotlin
val graph = TinkerGraph.open()
val g = graph.traversal()

```