package io.systemmesh.report;

import io.systemmesh.model.Finding;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TextReporter {
    public void print(List<Finding> findings, PrintStream out) {
        out.println("SystemMesh");
        out.println("==========");
        if (findings.isEmpty()) {
            out.println("No findings.");
            return;
        }

        for (Finding f : findings) {
            out.printf("%s %-8s %-11s %s:%d%n", f.ruleId(), f.severity(), f.category(), f.file(), f.line());
            out.println("  " + f.title());
            out.println("  " + f.message());
            out.println();
        }

        Map<String, Long> bySeverity = findings.stream()
                .collect(Collectors.groupingBy(f -> f.severity().name(), Collectors.counting()));
        out.println("Findings: " + findings.size() + " " + bySeverity);
    }
}
