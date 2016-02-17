package evolution_impl.gpprograms.base

import bgu.cs.evolution_engine.evolution.Individual
import evolution_impl.fitness.dummyagent.StateObservationWrapper

/**
 * Created by Itay on 20/04/2015.
 */
abstract class HeuristicIndividual extends Individual {
  def run[T](input: T): Double =  ???
  def compile()
}
