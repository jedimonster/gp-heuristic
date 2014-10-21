package evolution_engine.evolution;

import evolution_engine.fitness.FitnessResult;
import evolution_engine.mutators.Mutator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
public class ParentSelectionEvolutionStrategy<I extends Individual> extends BaseEvolutionStrategy<I> {

    public ParentSelectionEvolutionStrategy(EvolutionParameters evolutionParameters) {
        super(evolutionParameters);
    }

    @Override
    public List<I> evolve(List<I> individuals) {
        List<I> children = new ArrayList<>();

        // calculate fitness
        FitnessResult<I> fitnessResult = fitnessCalculator.calculateFitness(individuals);

        // select parents
        List<I> parents = selectionStrategy.select(individuals, fitnessResult);

        // be fruitful and multiply
        for (int i = 0; i + 1 < parents.size(); i += 2) {
            children.addAll(crossover.cross(parents.get(i), parents.get(i + 1)));
        }

        // mutate
        for (Mutator mutator : mutators) {
            mutator.mutate(children);
        }

        return children;
    }
}
