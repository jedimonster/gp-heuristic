package evolution_engine

import evolution_engine.evolution.{EvolutionParameters, EvolutionStrategy, Individual}

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class Run[I <: Individual] {
  def run(parameters: EvolutionParameters[I], evolutionStrategy: EvolutionStrategy[I]) {
    var initialPopulation: List[I] = parameters.getPopulationInitializer.getInitialPopulation(parameters.getPopulationSize)
    for (i <- 0 to parameters.getGenerations) {
      initialPopulation = evolutionStrategy.evolve(initialPopulation)
      System.out.print("Finished Generation %d\n".format(i))

    }
  }
}