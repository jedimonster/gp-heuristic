package evolution_impl


import evolution_engine.evolution.Individual
import evolution_engine.fitness.{FitnessCalculator, FitnessResult}
import evolution_impl.gpprograms.{CompilationException, JavaCodeIndividual}

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class EquationFitnessCalculator() extends FitnessCalculator[JavaCodeIndividual] {

  override def calculateFitness(individuals: java.util.List[JavaCodeIndividual]): FitnessResult[_ <: Individual] = {
    val fitnessValues = for (i <- individuals) yield (i, getIndividualFitness(i))
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }

  def getIndividualFitness(individual: JavaCodeIndividual): Double = {
    try {
      val samples: List[Double] = (for (i <- 0 until 20) yield Random.nextDouble()).toList
      val values = samples zip individual.getValues(samples)
      val diffs = for ((sample, value) <- values) yield Math.abs(value - getFunctionValue(sample)).toInt
      //      printf("fitness calculation, difference in values = %s\n", diffs)
      val fitness: Double = diffs.sum
      //      println("fitness = " + fitness)
      fitness
    } catch {
      case e: CompilationException => Double.NegativeInfinity
    }

  }


  def getFunctionValue(x: Double): Double = 3 * x *x  - 5 *x + 78
}
