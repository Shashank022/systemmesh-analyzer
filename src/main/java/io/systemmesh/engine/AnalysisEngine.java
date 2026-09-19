package io.systemmesh.engine;

import io.systemmesh.model.Finding;

import java.util.Comparator;
import java.util.List;

public final class AnalysisEngine {
    private final List<Rule> rules;

    public AnalysisEngine(List<Rule> rules) {
        this.rules = List.copyOf(rules);
    }

    public List<Finding> analyze(ProjectContext context) {
        return rules.stream()
                .flatMap(rule -> rule.analyze(context).stream())
                .sorted(Comparator
                        .comparing((Finding f) -> f.severity().rank()).reversed()
                        .thenComparing(Finding::ruleId)
                        .thenComparing(f -> f.file().toString())
                        .thenComparingInt(Finding::line))
                .toList();
    }

    public List<Rule> rules() {
        return rules;
    }
}
