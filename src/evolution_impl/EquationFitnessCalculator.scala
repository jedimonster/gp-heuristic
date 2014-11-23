package evolution_impl


import evolution_engine.evolution.Individual
import evolution_engine.fitness.{FitnessCalculator, FitnessResult}
import evolution_impl.gpprograms.{CompilationException, JavaCodeIndividual}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class EquationFitnessCalculator() extends FitnessCalculator[JavaCodeIndividual] {
  val samples: List[Double] = (for (i <- 0 until 5) yield Random.nextDouble() * 100 - 50).toList

  println("Initializing equation fitness calculator with points:")
  println(samples)
  println("Actual function values for said points:")
  println(for (sample <- samples) yield getFunctionValue(sample))

  override def calculateFitness(individuals: List[JavaCodeIndividual]): FitnessResult[JavaCodeIndividual] = {
    val fitnessValues = for (i <- individuals) yield (i, getIndividualFitness(i))
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }


  def getIndividualFitness(individual: JavaCodeIndividual): Double = {
    try {

      val values = samples zip individual.getValues(samples)
      val diffs = for ((sample, value) <- values) yield Math.abs(value - getFunctionValue(sample)).toInt
      //      printf("fitness calculation, difference in values = %s\n", diffs)
      val diffPerc: List[Double] = for ((sample, value) <- values) yield Math.abs(getFunctionValue(sample) - value)/Math.max(value, getFunctionValue(sample))
      var fitness: Double = new DescriptiveStatistics(diffPerc.toArray).getMean
//      var fitness: Double = diffs.sum
      if (fitness < 0) // overflow.
        fitness = Double.MaxValue
      //      println("fitness = " + fitness)
      fitness
    } catch {
      case e: CompilationException => {
        println("Compilation Exception:")
        e.printStackTrace()
        Double.PositiveInfinity
      }
    }

  }


  def getFunctionValue(x: Double): Double = 5.34 * x + 92
}
