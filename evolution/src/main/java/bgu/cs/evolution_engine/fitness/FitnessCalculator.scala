package bgu.cs.evolution_engine.fitness

import java.util.concurrent.ThreadPoolExecutor

import bgu.cs.evolution_engine.evolution.Individual
import evolution_impl.DumbFitnessResult
import evolution_impl.gpprograms.base.JavaCodeIndividual

import scala.collection.parallel.{ThreadPoolTaskSupport, ForkJoinTaskSupport}

/**
 * Created By Itay Azaria
 * Date: 2/26/14
 */
trait FitnessCalculator[I <: Individual] {
  protected var skipGen = false
  var gen = 0

  final def calculateFitness(individuals: List[I]): FitnessResult[I] = {
    val startTime = System.nanoTime()
    val parIndividuals = individuals.par
    val fitnessValues = parIndividuals.map(x => getIndividualFitness(x))
    val result = new DumbFitnessResult((individuals zip fitnessValues).toMap)
    val endTime = System.nanoTime()
    val diffTime = endTime - startTime

    if (skipGen) {
      printf("--\nSKIPPING USELESS FITNESS VALUES (GEN %d)\n--\n", gen)
      skipGen = false
      return calculateFitness(individuals)
    }

    printf("\n------\nGeneration time: %fs\n", diffTime * Math.pow(10, -9))
    processResult(result)

    result
  }

  def getIndividualFitness(individual: I): Double

  def processResult(result: FitnessResult[I])

  def skipCurrentGen(): Unit = {
    skipGen = true
  }
}