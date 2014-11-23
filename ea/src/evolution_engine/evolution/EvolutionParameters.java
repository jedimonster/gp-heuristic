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
    private int populationSize;


    private EvolutionLogger logger = null;
    private int generations;

    public EvolutionParameters(FitnessCalculator fitnessCalculator, SelectionStrategy selectionStrategy, Crossover<I> crossover, List<? extends Mutator> mutators, PopulationInitializer<I> populationInitializer, int generations, int populationSize) {
        this.fitnessCalculator = fitnessCalculator;
        this.selectionStrategy = selectionStrategy;
        this.crossover = crossover;
        this.mutators = mutators;
        this.populationInitializer = populationInitializer;
        this.generations = generations;
        this.populationSize = populationSize;
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

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }
}
