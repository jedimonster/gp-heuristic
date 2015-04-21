package controllers.heauristicGP

import java.util

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.IndividualHolder
import ontology.Types
import ontology.Types.ACTIONS
import tools.ElapsedCpuTimer

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
class Agent extends AbstractPlayer {
  protected var heuristic: GPHeuristic = null
  protected var statesEvaluated = 0
  protected var statesEvaluatedCounts = ListBuffer[Int]()
  protected var actions = 0
  protected var heuristicEvalTime = 0.0

  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
    heuristic = new GPHeuristic()
    heuristic.waitForFirstIndividual()


    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      //      IndividualHolder.aStarCache.clear()
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
  }

  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
//    heuristic.individual = IndividualHolder.bestIndividual
    statesEvaluated = 0

    // estimate amount of time available for each heuristic eval by evaluating the current state.
    val timer = new ElapsedCpuTimer()
    heuristic.evaluateState(stateObs)
    heuristicEvalTime = timer.elapsedMillis()

    val actionsScores = for (action <- stateObs.getAvailableActions) yield {
      val stateCopy: StateObservation = stateObs.copy
      stateCopy.advance(action)
      val newTimer = new ElapsedCpuTimer()
      newTimer.setMaxTimeMillis((elapsedTimer.remainingTimeMillis - 10) / stateObs.getAvailableActions.size)
      (action, evaluateStates(stateCopy, newTimer))
    }
    statesEvaluatedCounts :+= statesEvaluated
    actions += 1

    if (actions % 500 == 0) {
      printf("Average states evaluated by agent %f\n", statesEvaluatedCounts.sum.toDouble / statesEvaluatedCounts.size)
      statesEvaluatedCounts = ListBuffer[Int]()
    }
    actionsScores.maxBy(actionScore => actionScore._2)._1
  }

  /**
   * recursively evaluates the followup states, until out of time.
   * @param stateObservation
   * @param elapsedCpuTimer the best (score, heuristic score) that can be reached from each state.
   */
  def evaluateStates(stateObservation: StateObservation, elapsedCpuTimer: ElapsedCpuTimer): Tuple2[Double, Double] = {
    if (elapsedCpuTimer.remainingTimeMillis() <= heuristicEvalTime * 1.0 || stateObservation.isGameOver) {
      statesEvaluated += 1
      return (stateObservation.getGameScore, heuristic.evaluateState(stateObservation))
    }
    val availableActions: util.ArrayList[ACTIONS] = stateObservation.getAvailableActions
    val possible_scores = for (action <- availableActions) yield {
      val stateCopy: StateObservation = stateObservation.copy
      val newTimer = new ElapsedCpuTimer()

      stateCopy.advance(action)
      newTimer.setMaxTimeMillis(elapsedCpuTimer.remainingTimeMillis / availableActions.size)
      evaluateStates(stateCopy, newTimer)
    }
    possible_scores.maxBy(sh => if (sh._1 < 0) sh._1 else sh._2)
  }
}