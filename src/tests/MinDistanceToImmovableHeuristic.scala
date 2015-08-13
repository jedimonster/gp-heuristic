package tests

import controllers.Heuristics.StateHeuristic
import core.game.StateObservation
import evolution_engine.evolution.Individual
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.HeuristicIndividual

import scala.collection.JavaConversions._

/**
 * Created By Itay Azaria
 * Date: 12/07/2015
 */
class MinDistanceToImmovableHeuristic extends HeuristicIndividual {
  def evaluateState(stateObs: StateObservation): Double = {
    val wrapper = new StateObservationWrapper(stateObs)
    -1 * wrapper.getImmovableRealDistance.min
  }

  override def run(input: StateObservationWrapper): Double ={
    if(input.getImmovableRealDistance.isEmpty) {
      return -1 * input.getImmovableCount
    }
    -1 * input.getImmovableRealDistance.min - 50 * input.getImmovableCount

  }

  override def compile(): Unit = {}

  override def getName: String = {
    "TestMinDistance"
  }

  override def duplicate: Individual = this
}
