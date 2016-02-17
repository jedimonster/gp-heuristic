package bgu.cs.evolution_engine.evolution

import bgu.cs.evolution_engine.fitness.FitnessCalculator
import bgu.cs.evolution_engine.mutators.{Crossover, Mutator}
import bgu.cs.evolution_engine.selection.SelectionStrategy
import evolution_impl.fitness.dummyagent.StateObservationWrapper

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
abstract class BaseEvolutionStrategy[I <: Individual](
                                                       evolutionParameters: EvolutionParameters[I]
                                                       ) extends EvolutionStrategy[I] {
  val fitnessCalculator: FitnessCalculator[I] = evolutionParameters.getFitnessCalculator
  val selectionStrategy: SelectionStrategy[I] = evolutionParameters.getSelectionStrategy
  val crossover: Crossover[I] = evolutionParameters.getCrossover
  val mutators: List[Mutator[I]] = evolutionParameters.getMutators

}