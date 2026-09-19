package io.systemmesh.cli;

import io.systemmesh.engine.AnalysisEngine;
import io.systemmesh.engine.ProjectScanner;
import io.systemmesh.report.JsonReporter;
import io.systemmesh.report.TextReporter;
import io.systemmesh.rules.Rules;

import java.nio.file.Path;
import java.util.Arrays;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "help".equals(args[0]) || "--help".equals(args[0])) {
            usage();
            return;
        }

        AnalysisEngine engine = new AnalysisEngine(Rules.all());

        if ("rules".equals(args[0])) {
            engine.rules().forEach(rule ->
                    System.out.printf("%-14s %-11s %-8s %s%n",
                            rule.id(), rule.category(), rule.defaultSeverity(), rule.description()));
            return;
        }

        if (!"scan".equals(args[0])) {
            System.err.println("Unknown command: " + args[0]);
            usage();
            System.exit(2);
        }

        Path root = args.length >= 2 && !args[1].startsWith("--") ? Path.of(args[1]) : Path.of(".");
        String format = option(args, "--format", "text");

        var context = new ProjectScanner().scan(root.toAbsolutePath().normalize());
        var findings = engine.analyze(context);

        if ("json".equalsIgnoreCase(format)) {
            new JsonReporter().print(findings, System.out);
        } else {
            new TextReporter().print(findings, System.out);
        }

        String failOn = option(args, "--fail-on", null);
        if (failOn != null) {
            var threshold = io.systemmesh.model.Severity.valueOf(failOn.toUpperCase());
            boolean fail = findings.stream().anyMatch(f -> f.severity().rank() >= threshold.rank());
            if (fail) System.exit(1);
        }
    }

    private static String option(String[] args, String name, String fallback) {
        int i = Arrays.asList(args).indexOf(name);
        return i >= 0 && i + 1 < args.length ? args[i + 1] : fallback;
    }

    private static void usage() {
        System.out.println("""
                SystemMesh 0.1.0-SNAPSHOT

                Usage:
                  systemmesh scan [path] [--format text|json] [--fail-on LOW|MEDIUM|HIGH|CRITICAL]
                  systemmesh rules
                  systemmesh help
                """);
    }
}
