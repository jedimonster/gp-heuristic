package evolution_engine.evolution;

import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public interface PopulationInitializer<I extends Individual> {
    public List<I> getInitialPopulation(int n);
}
