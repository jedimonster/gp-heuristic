package evolution_engine.selection;

import evolution_engine.evolution.Individual;
import evolution_engine.fitness.FitnessResult;

import java.util.List;

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 16:07
 */
public interface SelectionStrategy<I extends Individual> {
    public List<I> select(List<I> previousGeneration, FitnessResult fitness);
}
