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
//class MinDistanceToImmovableHeuristic extends HeuristicIndividual {
//  //  override def evaluateState(stateObs: StateObservation): Double = {
//  //    val wrapper = new StateObservationWrapper(stateObs)
//  //    -1 * wrapper.getImmovableRealDistance(0, 0).min
//  //  }
//
//  override def run(input: StateObservationWrapper): Double =
//    -1 * input.getImmovableRealDistance().min
//
//  override def compile(): Unit = {}
//
//  override def getName: String = {"TestMinDistance"}
//
//  override def duplicate: Individual = this
//}
