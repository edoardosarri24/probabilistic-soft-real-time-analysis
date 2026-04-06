package config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import scheduler.deadlineMIssStrategy.AbortJobStrategy;
import scheduler.deadlineMIssStrategy.AbortSimulationStrategy;
import scheduler.deadlineMIssStrategy.ContinueStrategy;
import scheduler.deadlineMIssStrategy.DeadlineMissStrategy;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StrategyConfig.ContinueStrategyConfig.class, name = "continue"),
    @JsonSubTypes.Type(value = StrategyConfig.AbortJobStrategyConfig.class, name = "abortJob"),
    @JsonSubTypes.Type(value = StrategyConfig.AbortSimulationStrategyConfig.class, name = "abortSimulation")
})
public abstract class StrategyConfig {

    public abstract DeadlineMissStrategy toStrategy();

    public static class ContinueStrategyConfig extends StrategyConfig {
        @Override
        public DeadlineMissStrategy toStrategy() {
            return new ContinueStrategy();
        }
    }

    public static class AbortJobStrategyConfig extends StrategyConfig {
        @Override
        public DeadlineMissStrategy toStrategy() {
            return new AbortJobStrategy();
        }
    }

    public static class AbortSimulationStrategyConfig extends StrategyConfig {
        @Override
        public DeadlineMissStrategy toStrategy() {
            return new AbortSimulationStrategy();
        }
    }

}
