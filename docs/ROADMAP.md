# Roadmap

## v0.1 — foundation

- shared finding/rule model
- Java/config source scanner
- CLI
- text and JSON reporters
- starter multi-category rules
- CI

## v0.2 — System Graph

- service nodes
- method/call nodes
- dependency nodes
- Kafka topic edges
- configuration references
- graph serialization
- graph CLI command

## v0.3 — Impact Engine

- baseline vs candidate analysis
- new/resolved/unchanged findings
- changed-file to system-node mapping
- blast-radius calculation
- `systemmesh impact main..HEAD`

## v0.4 — Guardian

- SARIF
- GitHub pull-request annotations
- Maven plugin
- severity gates
- repository configuration file

## Rule-pack expansion

- retry amplification
- timeout ownership
- circuit-breaker gaps
- API compatibility
- cache key/eviction mismatch
- DB query amplification
- async context loss
- resource-pool mismatch
- circular service dependencies
- event contract drift
