package io.systemmesh.report;

import io.systemmesh.graph.SystemGraph;

import java.io.PrintStream;

public final class GraphTextReporter {
    public void print(SystemGraph graph, PrintStream out) {
        out.println("SystemMesh System Graph");
        out.println("=======================");
        out.printf("Nodes: %d  Edges: %d%n%n", graph.nodes().size(), graph.edges().size());

        out.println("NODES");
        for (var node : graph.nodes()) {
            out.printf("  %-12s %-28s %s%n", node.type(), node.id(), node.label());
        }

        out.println();
        out.println("EDGES");
        for (var edge : graph.edges()) {
            out.printf("  %-12s %s -> %s%n", edge.type(), edge.from(), edge.to());
        }
    }
}
