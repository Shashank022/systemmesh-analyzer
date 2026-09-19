package io.systemmesh;

import io.systemmesh.engine.AnalysisEngine;
import io.systemmesh.engine.ProjectScanner;
import io.systemmesh.rules.Rules;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaRuleTest {
    @Test
    void findsProducedTopicWithoutConsumer() throws Exception {
        var dir = Files.createTempDirectory("systemmesh-kafka");
        Files.writeString(dir.resolve("Producer.java"), """
                class Producer {
                  void send() {
                    kafkaTemplate.send("order.created", event);
                  }
                }
                """);

        var findings = new AnalysisEngine(Rules.all()).analyze(new ProjectScanner().scan(dir));
        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-EVENT-001")));
    }
}
