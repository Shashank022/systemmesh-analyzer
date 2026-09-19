package io.systemmesh.report;

import io.systemmesh.model.Finding;

import java.io.PrintStream;
import java.util.List;

public final class JsonReporter {
    public void print(List<Finding> findings, PrintStream out) {
        out.println("[");
        for (int i = 0; i < findings.size(); i++) {
            Finding f = findings.get(i);
            out.print("  {");
            out.print("\"ruleId\":\"" + escape(f.ruleId()) + "\",");
            out.print("\"severity\":\"" + escape(f.severity().name()) + "\",");
            out.print("\"category\":\"" + escape(f.category()) + "\",");
            out.print("\"title\":\"" + escape(f.title()) + "\",");
            out.print("\"message\":\"" + escape(f.message()) + "\",");
            out.print("\"file\":\"" + escape(f.file().toString()) + "\",");
            out.print("\"line\":" + f.line());
            out.print("}");
            if (i + 1 < findings.size()) out.print(",");
            out.println();
        }
        out.println("]");
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
