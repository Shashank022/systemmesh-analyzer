package io.systemmesh.rules;

import io.systemmesh.engine.Rule;

import java.util.List;

public final class Rules {
    private Rules() {}

    public static List<Rule> all() {
        return List.of(
                new DbCallInsideLoopRule(),
                new CommonPoolAsyncRule(),
                new CachedThreadPoolRule(),
                new RetryNonIdempotentRule(),
                new KafkaProducedWithoutConsumerRule(),
                new ActuatorWildcardRule()
        );
    }
}
