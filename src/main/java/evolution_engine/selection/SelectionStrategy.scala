package evolution_engine.selection

import evolution_engine.evolution.Individual
import evolution_engine.fitness.FitnessResult
import scala.collection.immutable.List

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 16:07
 */
trait SelectionStrategy[I <: Individual] {
  def select(previousGeneration: List[I], fitness: FitnessResult[I]): List[I]
}