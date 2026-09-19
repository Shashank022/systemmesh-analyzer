package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

final class DbCallInsideLoopRule implements Rule {
    private static final Pattern LOOP = Pattern.compile("\\b(for|while)\\s*\\(");
    private static final Pattern DB_CALL = Pattern.compile("\\b(repository|repo|dao|jdbcTemplate|entityManager)\\b.*\\.", Pattern.CASE_INSENSITIVE);

    public String id() { return "SM-CODE-001"; }
    public String category() { return "CODE"; }
    public String title() { return "Database access inside loop"; }
    public Severity defaultSeverity() { return Severity.HIGH; }
    public String description() { return "Detects repository/database calls close to loop bodies, a common N+1 and latency amplification pattern."; }

    public List<Finding> analyze(ProjectContext context) {
        List<Finding> findings = new ArrayList<>();
        for (var file : context.javaFiles()) {
            var lines = file.lines();
            for (int i = 0; i < lines.size(); i++) {
                if (!LOOP.matcher(lines.get(i)).find()) continue;
                int end = Math.min(lines.size(), i + 15);
                for (int j = i; j < end; j++) {
                    if (DB_CALL.matcher(lines.get(j)).find()) {
                        findings.add(new Finding(id(), defaultSeverity(), category(), title(),
                                "Database/repository access appears inside or immediately below a loop. Consider batching, joining, or prefetching data.",
                                file.path(), j + 1));
                        break;
                    }
                }
            }
        }
        return findings;
    }
}
