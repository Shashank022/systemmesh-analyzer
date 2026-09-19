package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;

final class CommonPoolAsyncRule implements Rule {
    public String id() { return "SM-CONC-001"; }
    public String category() { return "CONCURRENCY"; }
    public String title() { return "Implicit CompletableFuture common pool"; }
    public Severity defaultSeverity() { return Severity.MEDIUM; }
    public String description() { return "Detects async CompletableFuture calls without an explicit executor."; }

    public List<Finding> analyze(ProjectContext context) {
        List<Finding> findings = new ArrayList<>();
        for (var file : context.javaFiles()) {
            findings.addAll(RuleSupport.lineMatches(file, id(), defaultSeverity(), category(), title(),
                    "Async CompletableFuture call appears to use ForkJoinPool.commonPool(). Supply an application-owned executor for predictable isolation.",
                    line -> (line.contains("CompletableFuture.runAsync(") || line.contains("CompletableFuture.supplyAsync("))
                            && line.chars().filter(ch -> ch == ',').count() == 0));
        }
        return findings;
    }
}
