package evolution_engine.fitness

import evolution_engine.evolution.Individual
import evolution_impl.gpprograms.JavaCodeIndividual

/**
 * Created By Itay Azaria
 * Date: 2/26/14
 */
trait FitnessCalculator[I <: Individual] {
  def calculateFitness(individuals: List[I]): FitnessResult[I]
}