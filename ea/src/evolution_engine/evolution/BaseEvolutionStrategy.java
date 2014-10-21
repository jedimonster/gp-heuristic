package evolution_engine.evolution;

import evolution_engine.fitness.FitnessCalculator;
import evolution_engine.mutators.Crossover;
import evolution_engine.mutators.Mutator;
import evolution_engine.selection.SelectionStrategy;

import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
public abstract class BaseEvolutionStrategy<I extends Individual> implements EvolutionStrategy<I> {
    protected FitnessCalculator fitnessCalculator;
    protected SelectionStrategy selectionStrategy;
    protected Crossover<I> crossover;
    protected List<? extends Mutator> mutators;

    protected BaseEvolutionStrategy(EvolutionParameters evolutionParameters) {
        this.fitnessCalculator = evolutionParameters.getFitnessCalculator();
        this.selectionStrategy = evolutionParameters.getSelectionStrategy();
        this.crossover = evolutionParameters.getCrossover();
        this.mutators = evolutionParameters.getMutators();
    }
}
