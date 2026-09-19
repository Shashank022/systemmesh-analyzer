package io.systemmesh;

import io.systemmesh.engine.AnalysisEngine;
import io.systemmesh.engine.ProjectScanner;
import io.systemmesh.rules.Rules;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalysisEngineTest {
    @Test
    void findsRepresentativeSystemRisks() throws Exception {
        var dir = Files.createTempDirectory("systemmesh-test");
        Files.writeString(dir.resolve("Orders.java"), """
                class Orders {
                  void load() {
                    for (String id : ids) {
                      repository.findById(id);
                    }
                  }

                  void pool() {
                    java.util.concurrent.Executors.newCachedThreadPool();
                  }

                  void async() {
                    java.util.concurrent.CompletableFuture.supplyAsync(() -> "x");
                  }

                  @Retry(name = "payment")
                  void chargePayment() {}
                }
                """);
        Files.writeString(dir.resolve("application.properties"),
                "management.endpoints.web.exposure.include=*\n");

        var findings = new AnalysisEngine(Rules.all()).analyze(new ProjectScanner().scan(dir));

        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-CODE-001")));
        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-CONC-001")));
        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-CONC-002")));
        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-RES-001")));
        assertTrue(findings.stream().anyMatch(f -> f.ruleId().equals("SM-CONFIG-001")));
    }
}
