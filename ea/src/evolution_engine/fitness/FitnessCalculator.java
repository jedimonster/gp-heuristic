package evolution_engine.fitness;

import evolution_engine.evolution.Individual;

import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 2/26/14
 */
public interface FitnessCalculator<I extends Individual> {
    public FitnessResult calculateFitness(List<I> individuals);
}
