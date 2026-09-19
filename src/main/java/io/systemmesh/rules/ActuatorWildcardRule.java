package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.ArrayList;
import java.util.List;

final class ActuatorWildcardRule implements Rule {
    public String id() { return "SM-CONFIG-001"; }
    public String category() { return "CONFIG"; }
    public String title() { return "Wildcard Actuator endpoint exposure"; }
    public Severity defaultSeverity() { return Severity.HIGH; }
    public String description() { return "Detects Spring Boot Actuator include=* configuration."; }

    public List<Finding> analyze(ProjectContext context) {
        List<Finding> findings = new ArrayList<>();
        for (var file : context.configFiles()) {
            var lines = file.lines();
            for (int i = 0; i < lines.size(); i++) {
                String compact = lines.get(i).replace(" ", "");
                boolean properties = compact.contains("management.endpoints.web.exposure.include=*");
                boolean yamlInline = compact.matches("include:[\"']?\\*[\"']?");
                if (properties || yamlInline) {
                    findings.add(new Finding(id(), defaultSeverity(), category(), title(),
                            "All Actuator web endpoints appear exposed. Restrict exposure and enforce authentication/authorization for operational endpoints.",
                            file.path(), i + 1));
                }
            }
        }
        return findings;
    }
}
