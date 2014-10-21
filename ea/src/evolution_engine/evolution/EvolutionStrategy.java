package evolution_engine.evolution;

import java.util.List;

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 16:28
 */
public interface EvolutionStrategy<I extends Individual> {
    public List<I> evolve(List<I> individuals);
}
