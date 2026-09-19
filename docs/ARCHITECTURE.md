# SystemMesh architecture

SystemMesh is organized around a shared analysis pipeline:

```text
source tree
   |
   v
ProjectScanner
   |
   v
ProjectContext
   |
   v
AnalysisEngine
   |
   +--> code rules
   +--> linkage rules
   +--> data rules
   +--> resilience rules
   +--> event rules
   +--> config rules
   +--> cache rules
   +--> concurrency rules
   +--> architecture rules
   |
   v
Findings
   |
   +--> text
   +--> JSON
   +--> SARIF (planned)
   +--> PR Guardian (planned)
```

## Design principle

Rules should reason about system behavior, not formatting style.

The v0.1 scanner intentionally starts source-first. The next architectural step is a normalized graph model containing services, methods, dependencies, topics, configuration keys, data stores and call/event relationships. That graph will power cross-file and change-impact rules.
