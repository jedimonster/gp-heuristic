package evolution_impl

import java.util

import evolution_engine.evolution.Individual
import evolution_engine.fitness.{FitnessResult, FitnessCalculator}
import collection.JavaConversions._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class EquationFitnessCalculator() extends FitnessCalculator[JavaCodeIndividual] {

  override def calculateFitness(individuals: util.List[JavaCodeIndividual]): FitnessResult[_ <: Individual] = {
    val fitnessValues = for (i <- individuals) yield (i, getIndividualFitness(i))
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }

  def getIndividualFitness(individual: JavaCodeIndividual): Double = {
    val samples: List[Int] = (-2 to 5).toList
    val values = samples zip individual.getValues(samples)
    val diffs = for ((sample, value) <- values) yield Math.abs(value - getFunctionValue(sample)).toInt
    printf("fitness calculation, difference in values = %s\n", diffs)
    diffs.sum
  }


  def getFunctionValue(x: Double): Double = 3 * x - 5
}
