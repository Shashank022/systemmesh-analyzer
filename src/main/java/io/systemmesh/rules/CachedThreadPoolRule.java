package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;

final class CachedThreadPoolRule implements Rule {
    public String id() { return "SM-CONC-002"; }
    public String category() { return "CONCURRENCY"; }
    public String title() { return "Unbounded cached thread pool"; }
    public Severity defaultSeverity() { return Severity.HIGH; }
    public String description() { return "Detects Executors.newCachedThreadPool(), which can create an unbounded number of platform threads."; }

    public List<Finding> analyze(ProjectContext context) {
        List<Finding> findings = new ArrayList<>();
        for (var file : context.javaFiles()) {
            findings.addAll(RuleSupport.lineMatches(file, id(), defaultSeverity(), category(), title(),
                    "Executors.newCachedThreadPool() is effectively unbounded. Prefer a bounded executor or a concurrency model with an explicit resource budget.",
                    line -> line.contains("Executors.newCachedThreadPool(")));
        }
        return findings;
    }
}
