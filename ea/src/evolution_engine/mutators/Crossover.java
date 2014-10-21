package evolution_engine.mutators;

import evolution_engine.evolution.Individual;

import java.util.List;

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 15:57
 */
public interface Crossover<I extends Individual> {


    public abstract List<I> cross(I father, I mother);

    public double getProbability();
}
