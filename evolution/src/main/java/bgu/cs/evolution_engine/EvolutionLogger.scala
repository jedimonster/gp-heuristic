package bgu.cs.evolution_engine

import bgu.cs.evolution_engine.evolution.Individual
import bgu.cs.evolution_engine.fitness.FitnessResult

/**
 * Created by itayaza on 27/10/2014.
 */
trait EvolutionLogger[I <: Individual] {
  def addGeneration(individuals: List[I], fitness: FitnessResult[I])
}