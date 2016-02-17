package bgu.cs.evolution_engine.evolution

import bgu.cs.evolution_engine.fitness.FitnessResult

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
class ParentSelectionEvolutionStrategy[I <: Individual](evolutionParameters: EvolutionParameters[I])
  extends BaseEvolutionStrategy[I](evolutionParameters) {

  def evolve(individuals: List[I]): List[I] = {
    var children: IndexedSeq[I] = Vector.empty
    val fitnessResult: FitnessResult[I] = fitnessCalculator.calculateFitness(individuals)

    val bestIndividual: I = individuals.maxBy(i => fitnessResult.getFitness(i))
    evolutionParameters.synchronized {
      evolutionParameters.bestIndividual = Option(bestIndividual)
      evolutionParameters.notifyAll() // if anyone was waiting..
    }
    if (evolutionParameters.isLoggingEnable) {
      evolutionParameters.getLogger.addGeneration(individuals, fitnessResult)
    }
    val parents: IndexedSeq[I] = selectionStrategy.select(individuals, fitnessResult).toIndexedSeq
    var i: Int = 0

    while (i + 1 < parents.size) {
      if (Math.random() < crossover.getProbability) {
        val cross: List[I] = crossover.cross(parents(i), parents(i + 1))
        children ++= cross
      } else {
        children ++= IndexedSeq(parents(i).duplicate.asInstanceOf[I], parents(i + 1).duplicate.asInstanceOf[I])
      }

      i += 2
    }

    import scala.collection.JavaConversions._
    for (mutator <- mutators) {
      mutator.mutate(children)
    }
    children.toList
  }
}