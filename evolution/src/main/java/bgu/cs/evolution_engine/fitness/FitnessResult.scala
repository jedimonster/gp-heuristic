package bgu.cs.evolution_engine.fitness

import bgu.cs.evolution_engine.evolution.Individual

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
trait FitnessResult[I <: Individual] {
  def getFitness(individual: I): Double

  def getMap: Map[I, Double]
}