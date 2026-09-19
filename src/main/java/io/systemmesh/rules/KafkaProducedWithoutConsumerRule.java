package io.systemmesh.rules;

import io.systemmesh.engine.ProjectContext;
import io.systemmesh.engine.Rule;
import io.systemmesh.model.Finding;
import io.systemmesh.model.Severity;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class KafkaProducedWithoutConsumerRule implements Rule {
    private static final Pattern SEND = Pattern.compile("\\.send\\(\\s*\"([^\"]+)\"");
    private static final Pattern TOPICS = Pattern.compile("topics\\s*=\\s*\"([^\"]+)\"");

    public String id() { return "SM-EVENT-001"; }
    public String category() { return "EVENTS"; }
    public String title() { return "Kafka topic produced but never consumed"; }
    public Severity defaultSeverity() { return Severity.MEDIUM; }
    public String description() { return "Builds a basic source-level Kafka topic map and finds produced topics without scanned consumers."; }

    public List<Finding> analyze(ProjectContext context) {
        Map<String, Location> producers = new LinkedHashMap<>();
        Set<String> consumers = new HashSet<>();

        for (var file : context.javaFiles()) {
            for (int i = 0; i < file.lines().size(); i++) {
                String line = file.lines().get(i);
                Matcher send = SEND.matcher(line);
                if (send.find()) producers.putIfAbsent(send.group(1), new Location(file.path(), i + 1));
                Matcher topics = TOPICS.matcher(line);
                if (topics.find()) consumers.add(topics.group(1));
            }
        }

        List<Finding> findings = new ArrayList<>();
        for (var entry : producers.entrySet()) {
            if (!consumers.contains(entry.getKey())) {
                findings.add(new Finding(id(), defaultSeverity(), category(), title(),
                        "Topic '" + entry.getKey() + "' is produced but no @KafkaListener consumer for that literal topic was found in the scanned source tree.",
                        entry.getValue().path(), entry.getValue().line()));
            }
        }
        return findings;
    }

    private record Location(Path path, int line) {}
}
