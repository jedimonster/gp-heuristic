package evolution_engine;

import evolution_engine.evolution.Individual;
import evolution_engine.fitness.FitnessResult;

import java.util.List;

/**
 * Created by itayaza on 27/10/2014.
 */
public interface EvolutionLogger {
    public void addGeneration(List<? extends Individual> individuals, FitnessResult fitness);
}
