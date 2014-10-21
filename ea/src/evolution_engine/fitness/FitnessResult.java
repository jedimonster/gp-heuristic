package evolution_engine.fitness;

import evolution_engine.evolution.Individual;

import java.util.Map;

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
public interface FitnessResult<I extends Individual> {
    public double getFitness(I individual);
}
