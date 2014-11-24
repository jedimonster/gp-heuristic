package evolution_engine.evolution

import evolution_engine.fitness.FitnessResult

import scala.collection.mutable.ListBuffer

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
class ParentSelectionEvolutionStrategy[I <: Individual](evolutionParameters: EvolutionParameters[I])
  extends BaseEvolutionStrategy[I](evolutionParameters) {

  def evolve(individuals: List[I]): List[I] = {
    var children: IndexedSeq[I] = Vector.empty

    val fitnessResult: FitnessResult[I] = fitnessCalculator.calculateFitness(individuals)

    if (evolutionParameters.isLoggingEnable) {
      evolutionParameters.getLogger.addGeneration(individuals, fitnessResult)
    }
    val parents: IndexedSeq[I] = selectionStrategy.select(individuals, fitnessResult).toIndexedSeq
    var i: Int = 0

    while (i + 1 < parents.size) {
      val cross: List[I] = crossover.cross(parents(i), parents(i + 1))
      children ++= cross

      i += 2
    }

    import scala.collection.JavaConversions._
    for (mutator <- mutators) {
      mutator.mutate(children)
    }
    children.toList
  }
}