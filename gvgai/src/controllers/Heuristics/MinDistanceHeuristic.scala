package controllers.Heuristics

import core.game.StateObservation
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import scalaj.collection.Imports._

/**
 * Created by Itay on 25/03/2015.
 */
class MinDistanceHeuristic(stateObservation: StateObservation) extends StateHeuristic {
  override def evaluateState(stateObs: StateObservation): Double = {
    val start = System.nanoTime()
    val wrappedState = new StateObservationWrapper(stateObs)
    val distances = wrappedState.getPortalsHeuristicDistance.asScala

    var acc = 0.0

    for (d <- distances)
      acc += d

    val end = System.nanoTime()
//    printf("time took for heuristica eval = %fms\n", (end - start) * Math.pow(10, -9))
    1 / acc
  }
}
