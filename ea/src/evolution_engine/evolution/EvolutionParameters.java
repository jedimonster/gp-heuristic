package evolution_engine.evolution;

import evolution_engine.EvolutionLogger;
import evolution_engine.fitness.FitnessCalculator;
import evolution_engine.mutators.Crossover;
import evolution_engine.mutators.Mutator;
import evolution_engine.selection.SelectionStrategy;

import java.util.List;

public class EvolutionParameters<I extends Individual> {
    private final FitnessCalculator fitnessCalculator;
    private final SelectionStrategy selectionStrategy;
    private final Crossover<I> crossover;
    private final List<? extends Mutator> mutators;
    private final PopulationInitializer<I> populationInitializer;
    private EvolutionLogger logger = null;

    public EvolutionParameters(FitnessCalculator fitnessCalculator, SelectionStrategy selectionStrategy, Crossover<I> crossover, List<? extends Mutator> mutators, PopulationInitializer<I> populationInitializer) {
        this.fitnessCalculator = fitnessCalculator;
        this.selectionStrategy = selectionStrategy;
        this.crossover = crossover;
        this.mutators = mutators;
        this.populationInitializer = populationInitializer;
    }

    public FitnessCalculator getFitnessCalculator() {
        return fitnessCalculator;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public Crossover<I> getCrossover() {
        return crossover;
    }

    public List<? extends Mutator> getMutators() {
        return mutators;
    }

    public PopulationInitializer<I> getPopulationInitializer() {
        return populationInitializer;
    }

    public boolean isLoggingEnable() {
        return logger != null;
    }

    public EvolutionLogger getLogger() {

        return logger;
    }

    public void setLogger(EvolutionLogger logger) {
        this.logger = logger;
    }
}
