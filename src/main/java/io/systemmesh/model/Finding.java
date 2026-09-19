package io.systemmesh.model;

import java.nio.file.Path;

public record Finding(
        String ruleId,
        Severity severity,
        String category,
        String title,
        String message,
        Path file,
        int line) {
}
