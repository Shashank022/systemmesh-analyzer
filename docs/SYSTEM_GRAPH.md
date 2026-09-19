# System Graph

SystemMesh turns a source tree into a normalized graph that later analysis can reason over.

## Current node types

- SERVICE
- JAVA_CLASS
- KAFKA_TOPIC
- CONFIG_KEY
- DATA_STORE

## Current edge types

- CONTAINS
- PRODUCES
- CONSUMES
- READS_CONFIG
- ACCESSES
- CALLS

## Example

```text
service:checkout-service
    |
    +-- CONTAINS --> class:demo.checkout.CheckoutClient
                           |
                           +-- CALLS ------> service:payment-service
                           +-- PRODUCES ---> topic:order.created
                           +-- CONSUMES ---> topic:payment.completed
                           +-- ACCESSES ---> data:database
                           +-- READS_CONFIG -> config:PAYMENT_URL
```

## CLI

```bash
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar graph .
java -jar target/systemmesh-analyzer-0.1.0-SNAPSHOT.jar graph . --format json
```

The graph is deterministic and source-derived. Later releases can enrich it with bytecode, build-tool, Kubernetes, OpenAPI, schema-registry, and runtime evidence.
