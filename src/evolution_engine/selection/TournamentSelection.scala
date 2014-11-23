package evolution_engine.selection


import evolution_engine.evolution.Individual
import evolution_engine.fitness.FitnessResult

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by itayaza on 19/11/2014.
 */

class TournamentSelection[T <: Individual](minimize: Boolean) extends SelectionStrategy[T] {
  val subsetSize = 16

  def optimal(list: List[T], value: FitnessResult[T]): T = {
    var optFitness = if (minimize) Double.MaxValue else Double.MinValue
    var opt: Option[T] = None


    for (i <- 0 to list.size - 1) {
      val individual: T = list.get(i)
      if (minimize && value.getFitness(individual) < optFitness
        || value.getFitness(individual) > optFitness) {
        optFitness = value.getFitness(individual)
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
      val subList = for (i <- 0 to subsetSize) yield previousGeneration(Random.nextInt(n))

      // pick highest fitness
      selected :+= optimal(subList.toList, fitness)
    }

    selected.toList
  }
}
