package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class RetryNonIdempotentRule implements Rule {
    public String id() { return "SM-RES-001"; }
    public String category() { return "RESILIENCE"; }
    public String title() { return "Retry on potentially non-idempotent operation"; }
    public Severity defaultSeverity() { return Severity.HIGH; }
    public String description() { return "Flags retry-protected methods whose names suggest mutation/payment/order side effects."; }

    public List<Finding> analyze(ProjectContext context) {
        List<Finding> findings = new ArrayList<>();
        for (var file : context.javaFiles()) {
            var lines = file.lines();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).contains("@Retry")) continue;
                for (int j = i + 1; j < Math.min(lines.size(), i + 6); j++) {
                    String lower = lines.get(j).toLowerCase(Locale.ROOT);
                    if (lower.matches(".*\\b(create|submit|charge|pay|purchase|place|send|delete|update|save)[a-z0-9_]*\\s*\\(.*")) {
                        findings.add(new Finding(id(), defaultSeverity(), category(), title(),
                                "A retry appears to wrap a state-changing operation. Verify idempotency keys or deduplication before retrying.",
                                file.path(), j + 1));
                        break;
                    }
                }
            }
        }
        return findings;
    }
}
