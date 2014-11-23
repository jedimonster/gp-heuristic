package evolution_engine.evolution

import evolution_engine.fitness.FitnessCalculator
import evolution_engine.mutators.{Crossover, Mutator}
import evolution_engine.selection.SelectionStrategy

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