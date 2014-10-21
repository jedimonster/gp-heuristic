package evolution_engine.mutators;

import evolution_engine.evolution.Individual;

import java.util.List;

/**
 * Created by Itay Azaria
 * Date: 02/03/14
 * Time: 19:35
 */
public interface Mutator<I extends Individual> {


    /**
     * mutates the given features according to their fitness and appropriate strategy.
     *
     * @param features map of haar features and their fitness (between 0 to 1)
     * @return list of the mutated haar features
     */
    public abstract List<I> mutate(List<I> features);

    public double getProbability();
}
