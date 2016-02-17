package evolution_impl

import bgu.cs.evolution_engine.evolution.Individual
import bgu.cs.evolution_engine.fitness.FitnessResult

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class DumbFitnessResult[I <: Individual](val fitnessValues: Map[I, Double]) extends FitnessResult[I] {
  override def getFitness(individual: I): Double = fitnessValues(individual)

  override def getMap: Map[I, Double] = fitnessValues
}
