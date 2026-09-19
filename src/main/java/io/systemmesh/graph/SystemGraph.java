package io.systemmesh.graph;

import java.util.List;

public record SystemGraph(List<GraphNode> nodes, List<GraphEdge> edges) {
    public SystemGraph {
        nodes = List.copyOf(nodes);
        edges = List.copyOf(edges);
    }

    public long count(NodeType type) {
        return nodes.stream().filter(node -> node.type() == type).count();
    }
}
