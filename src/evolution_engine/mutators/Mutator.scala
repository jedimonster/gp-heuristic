package evolution_engine.mutators

import java.util.List

import evolution_engine.evolution.Individual

/**
 * Created by Itay Azaria
 * Date: 02/03/14
 * Time: 19:35
 */
trait Mutator[I <: Individual] {
  /**
   * mutates the given features according to their fitness and appropriate strategy.
   *
   * @param features map of haar features and their fitness (between 0 to 1)
   * @return list of the mutated haar features
   */
  def mutate(features: List[I]): List[I]

  def getProbability: Double
}