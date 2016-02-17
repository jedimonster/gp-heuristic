package bgu.cs.evolution_engine.mutators

import bgu.cs.evolution_engine.evolution.Individual

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 15:57
 */
trait Crossover[I <: Individual] {
  def cross(father: I, mother: I): List[I]

  def getProbability: Double
}