package example.micronaut;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

public class GremlinHelper {
    public static GraphTraversal<?, Vertex> v(Object id) {
        return __.V(id);
    }
    public static GraphTraversal<Vertex, Edge> outE(String label) {
        return __.outE(label);
    }
    public static GraphTraversal<Vertex, Edge> addE(String label) {
        return __.addE(label);
    }
    public static GraphTraversal<Edge, Vertex> inV() {
        return __.inV();
    }
}
