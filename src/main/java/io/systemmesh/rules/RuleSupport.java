package io.systemmesh.rules;

import io.systemmesh.engine.SourceFile;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

final class RuleSupport {
    private RuleSupport() {}

    static List<Finding> lineMatches(
            SourceFile file,
            String ruleId,
            Severity severity,
            String category,
            String title,
            String message,
            Predicate<String> predicate) {
        List<Finding> findings = new ArrayList<>();
        for (int i = 0; i < file.lines().size(); i++) {
            if (predicate.test(file.lines().get(i))) {
                findings.add(new Finding(ruleId, severity, category, title, message, file.path(), i + 1));
            }
        }
        return findings;
    }
}
