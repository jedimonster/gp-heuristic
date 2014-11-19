package evolution_impl.selection

import java.util

import evolution_engine.evolution.Individual
import evolution_engine.fitness.FitnessResult
import evolution_engine.selection.SelectionStrategy

import scala.util.Random

/**
 * Created by itayaza on 19/11/2014.
 */
class TournamentSelection[T]  extends SelectionStrategy[T]{
  val subsetSize = 16

  def max(list: util.List[T], value: FitnessResult[_ <: Individual]): T = {
    var max : Double =
  }

  override def select(previousGeneration: util.List[T], fitness: FitnessResult[_ <: Individual]): util.List[T] = {
    val n = previousGeneration.size()
    val selected : util.List[T] = new util.ArrayList[T](n)

    while(selected.size() < n) {
      // get a random n sized subset
      val shuffled: util.List[T] = Random.shuffle(previousGeneration)
      val subList: util.List[T] = shuffled.subList(0, subsetSize)

      // pick highest fitness
      selected.add(max(subList, fitness))
    }
    ???
  }
}
