package io.systemmesh.engine;

import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.util.List;

public interface Rule {
    String id();
    String category();
    String title();
    Severity defaultSeverity();
    String description();
    List<Finding> analyze(ProjectContext context);
}
