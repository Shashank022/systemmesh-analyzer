package io.systemmesh.graph;

import java.util.Map;

public record GraphEdge(String from, String to, EdgeType type, Map<String, String> attributes) {
    public GraphEdge {
        attributes = Map.copyOf(attributes);
    }
}
