package io.systemmesh.graph;

import java.util.Map;

public record GraphNode(String id, NodeType type, String label, Map<String, String> attributes) {
    public GraphNode {
        attributes = Map.copyOf(attributes);
    }
}
