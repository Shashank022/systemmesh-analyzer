package io.systemmesh.report;

import io.systemmesh.graph.GraphEdge;
import io.systemmesh.graph.GraphNode;
import io.systemmesh.graph.SystemGraph;

import java.io.PrintStream;
import java.util.Map;

public final class GraphJsonReporter {
    public void print(SystemGraph graph, PrintStream out) {
        out.println("{");
        out.println("  \"nodes\": [");
        for (int i = 0; i < graph.nodes().size(); i++) {
            GraphNode n = graph.nodes().get(i);
            out.print("    {\"id\":\"" + esc(n.id()) + "\",\"type\":\"" + n.type()
                    + "\",\"label\":\"" + esc(n.label()) + "\",\"attributes\":");
            printMap(n.attributes(), out);
            out.print("}");
            if (i + 1 < graph.nodes().size()) out.print(",");
            out.println();
        }
        out.println("  ],");
        out.println("  \"edges\": [");
        for (int i = 0; i < graph.edges().size(); i++) {
            GraphEdge e = graph.edges().get(i);
            out.print("    {\"from\":\"" + esc(e.from()) + "\",\"to\":\"" + esc(e.to())
                    + "\",\"type\":\"" + e.type() + "\",\"attributes\":");
            printMap(e.attributes(), out);
            out.print("}");
            if (i + 1 < graph.edges().size()) out.print(",");
            out.println();
        }
        out.println("  ]");
        out.println("}");
    }

    private void printMap(Map<String, String> map, PrintStream out) {
        out.print("{");
        int i = 0;
        for (var entry : map.entrySet()) {
            if (i++ > 0) out.print(",");
            out.print("\"" + esc(entry.getKey()) + "\":\"" + esc(entry.getValue()) + "\"");
        }
        out.print("}");
    }

    private String esc(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
