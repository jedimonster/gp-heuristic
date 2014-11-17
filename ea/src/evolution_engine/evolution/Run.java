package evolution_engine.evolution;

import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Run<I extends Individual> {
    public void run(EvolutionParameters<I> parameters, EvolutionStrategy<I> evolutionStrategy) {
        List<I> initialPopulation = parameters.getPopulationInitializer().getInitialPopulation(100);
        for (int i = 0; i < 50; i++) {
            initialPopulation = evolutionStrategy.evolve(initialPopulation);
        }
    }
}
