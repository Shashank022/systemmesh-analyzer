package io.systemmesh;

import io.systemmesh.engine.ProjectScanner;
import io.systemmesh.graph.EdgeType;
import io.systemmesh.graph.NodeType;
import io.systemmesh.graph.SystemGraphBuilder;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemGraphBuilderTest {
    @Test
    void buildsCrossConcernSystemGraph() throws Exception {
        var dir = Files.createTempDirectory("systemmesh-graph");
        String config = "spring.application.name=checkout-service\n"
                + "payment.url=$" + "{PAYMENT_URL:http://localhost:8081}\n";
        Files.writeString(dir.resolve("application.properties"), config);

        Files.writeString(dir.resolve("CheckoutClient.java"), """
                package demo.checkout;

                @FeignClient(name = "payment-service")
                class CheckoutClient {
                    @KafkaListener(topics = "payment.completed")
                    void consume(String event) {}

                    String run() {
                        kafkaTemplate.send("order.created", event);
                        repository.findById(id);
                        return "ok";
                    }
                }
                """);

        var context = new ProjectScanner().scan(dir);
        var graph = new SystemGraphBuilder().build(context);

        assertTrue(graph.nodes().stream().anyMatch(n ->
                n.type() == NodeType.SERVICE && n.id().equals("service:checkout-service")));
        assertTrue(graph.nodes().stream().anyMatch(n ->
                n.type() == NodeType.KAFKA_TOPIC && n.id().equals("topic:order.created")));
        assertTrue(graph.nodes().stream().anyMatch(n ->
                n.type() == NodeType.CONFIG_KEY && n.id().equals("config:PAYMENT_URL")));
        assertTrue(graph.nodes().stream().anyMatch(n -> n.type() == NodeType.DATA_STORE));
        assertTrue(graph.edges().stream().anyMatch(e -> e.type() == EdgeType.PRODUCES));
        assertTrue(graph.edges().stream().anyMatch(e -> e.type() == EdgeType.CONSUMES));
        assertTrue(graph.edges().stream().anyMatch(e -> e.type() == EdgeType.CALLS
                && e.to().equals("service:payment-service")));
        assertTrue(graph.edges().stream().anyMatch(e -> e.type() == EdgeType.ACCESSES));
    }
}
