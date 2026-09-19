# SystemMesh

**Understand how your system behaves before you ship.**

SystemMesh is a static system-analysis platform for Java and distributed applications. It looks beyond isolated code style and analyzes patterns that can become runtime, resilience, event-driven, configuration, and architecture problems.

## v0.1 foundation

SystemMesh provides a shared rule engine, CLI, text/JSON reporting, starter rules, and a normalized **System Graph**.

### Starter rules

| Rule | Severity | Category | Detects |
|---|---|---|---|
| SM-CODE-001 | HIGH | CODE | Repository/database access inside loops |
| SM-CONC-001 | MEDIUM | CONCURRENCY | CompletableFuture async operations without an explicit executor |
| SM-CONC-002 | HIGH | CONCURRENCY | Unbounded cached thread pools |
| SM-RES-001 | HIGH | RESILIENCE | Retry annotations on methods that appear non-idempotent |
| SM-EVENT-001 | MEDIUM | EVENTS | Kafka topics produced but never consumed in the scanned source tree |
| SM-CONFIG-001 | HIGH | CONFIG | Spring Actuator wildcard endpoint exposure |

## Build

```bash
mvn clean test package
```

## Scan

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar scan .
```

## Build the System Graph

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar graph .
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar graph . --format json
```

The current graph connects services, Java classes, Kafka topics, configuration references, database access, and Feign downstream services.

## Direction

Next comes **Change Impact**: compare a baseline with a candidate change and determine which nodes and relationships are newly affected.

The long-term PR Guardian question is:

> What could this change break or degrade when it is deployed?
