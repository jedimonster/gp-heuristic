package tests

import java.lang
import java.lang.Iterable

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

  override def run[T <: StateObservationWrapper](input: T): Double = {
    //    if(input.getImmovableRealDistance.isEmpty) {
    //      return -1 * input.getImmovableCount
    //    }
    //    -1 * input.getImmovableRealDistance.min - 50 * input.getImmovableCount

    val distances: Iterable[lang.Double] = input.getNPCHeuristicDistance
    val realDistances: Iterable[lang.Double] = input.getNPCRealDistance

    val minRealDistance: Double = if (realDistances.nonEmpty) realDistances.min else 1.0
    val minHeuristic: Double = if (distances.nonEmpty) distances.min else 1.0
    //    var minHeuristic = 0.0
    //    for (distance <- input.getNPCRealDistance) {
    //      minHeuristic += distance
    //    }
    //    (1 / input.getHeuristicDistanceBetweenTypes(5, 7)) + 1.0 / minHeuristic
    //    + (1.0 / minRealDistance)
    //    1.0 / minHeuristic
    val resoucesDistances: Iterable[lang.Double] = input.getResourcesRealDistance
    var totalDistance = 0.0
    for (d <- resoucesDistances)
      totalDistance += d
    if (resoucesDistances isEmpty) {
      1.0 / input.getPortalRealDistance.iterator().next()
    } else {
      -1 * (resoucesDistances.min + 50 * input.getResourcesCount)
    }

  }

  override def compile(): Unit = {}

  override def getName: String = {
    "TestMinDistance"
  }

  override def duplicate: Individual = this
}
