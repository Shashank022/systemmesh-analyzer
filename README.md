# SystemMesh

**Understand how your system behaves before you ship.**

SystemMesh is a static system-analysis platform for Java and distributed applications. It looks beyond isolated code style and analyzes patterns that can become runtime, resilience, event-driven, configuration, and architecture problems.

## v0.1 foundation

The first version provides a shared rule engine, CLI, text/JSON reporting, and starter rules across several system concerns.

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

## Run

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar scan .
```

JSON output:

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar scan . --format json
```

List rules:

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar rules
```

## Direction

SystemMesh will grow around one shared system model with rule packs for:

- Java / Spring
- dependency and binary linkage
- JPA / SQL / data access
- resilience and failure amplification
- Kafka and event-driven systems
- Spring / Helm / Kubernetes configuration
- caching
- concurrency and resource budgets
- architecture and change-impact analysis

The long-term goal is a PR Guardian that answers:

> What could this change break or degrade when it is deployed?
