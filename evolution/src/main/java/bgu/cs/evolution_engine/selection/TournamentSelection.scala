package bgu.cs.evolution_engine.selection


import bgu.cs.evolution_engine.evolution.Individual
import bgu.cs.evolution_engine.fitness.FitnessResult

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by itayaza on 19/11/2014.
 */

class TournamentSelection[T <: Individual](minimize: Boolean) extends SelectionStrategy[T] {
  val subsetSize = 3

  def optimal(individuals: List[T], fitnessResult: FitnessResult[T]): T = {
    var optFitness = if (minimize) Double.PositiveInfinity else Double.NegativeInfinity
    var opt: Option[T] = None


    for (individual <- individuals) {
      val individualFitness = fitnessResult.getFitness(individual)
      if (minimize && individualFitness <= optFitness
        || individualFitness >= optFitness) {
        optFitness = individualFitness
        opt = Option(individual)
      }
    }
    opt match {
      case Some(t) => t
      case None => throw new RuntimeException("error calculating optimal indivudal")
    }
  }


  def select(previousGeneration: List[T], fitness: FitnessResult[T]): List[T] = {
    val n = previousGeneration.size
    var selected: ListBuffer[T] = ListBuffer()

    while (selected.size < n) {
      // get a random n sized subset
      //      val shuffled: util.List[T] = Random.shuffle(previousGeneration)
      //      val subList: util.List[T] = shuffled.subList(0, subsetSize)

      // shuffle subsetSize individuals:
      val subList = for (i <- 0 to subsetSize - 1) yield previousGeneration(Random.nextInt(n))

      // pick highest fitness
      selected :+= optimal(subList.toList, fitness)
    }

    selected.toList
  }
}
