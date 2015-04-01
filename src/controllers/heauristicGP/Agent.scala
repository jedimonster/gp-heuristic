package controllers.heauristicGP

import java.util

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{IndividualHolder, IndividualHolder$}
import evolution_impl.search.{Position, AStar}
import ontology.Types
import ontology.Types.ACTIONS
import tools.ElapsedCpuTimer
import scala.annotation.tailrec
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
class Agent extends AbstractPlayer {
  protected var heuristic: GPHeuristic = null
  protected val aStar : AStar[Position] = new AStar[Position]() // each agent has his own AStar with cache, new game/level = new agent.

  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
    heuristic = new GPHeuristic(null)
    heuristic.waitForFirstIndividual()


    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
//      IndividualHolder.aStarCache.clear()
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
  }

  /**
   * Very simple one step lookahead agent.
   *
   * @param stateObs     Observation of the current state.
   * @param elapsedTimer Timer when the action returned is due.
   * @return An action for the current state
   */
  //  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
  //    var bestAction: Types.ACTIONS = null
  //    var maxQ: Double = Double.NegativeInfinity
  //    var ended = false
  //    IndividualHolder.currentState = stateObs.copy
  //
  //    for (action <- stateObs.getAvailableActions) {
  //      val stCopy: StateObservation = stateObs.copy
  //      stCopy.advance(action)
  //      val Q: Double = heuristic.evaluateState(stCopy)
  //      if (Q > maxQ) {
  //        maxQ = Q
  //        bestAction = action
  //        ended = stCopy.isGameOver
  //      }
  //    }
  //    if (ended) {
  //      // we need to stop looking at it so the framework doesn't break. todo stop all working threads?
  //      IndividualHolder.synchronized {
  //        IndividualHolder.currentState = null
  //      }
  //
  //    }
  //
  //    while (elapsedTimer.remainingTimeMillis > 10) {
  //
  //    }
  //
  //    return bestAction
  //  }
  var statesEvaluated = 0

  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    var bestAction: Types.ACTIONS = null
    var maxHeursticVal: Double = Double.NegativeInfinity
    var ended = false
    IndividualHolder.currentState = stateObs.copy
    statesEvaluated = 0

    val actionsScores = for (action <- stateObs.getAvailableActions) yield {
      val stateCopy: StateObservation = stateObs.copy
      stateCopy.advance(action)
      val newTimer = new ElapsedCpuTimer()
      newTimer.setMaxTimeMillis((elapsedTimer.remainingTimeMillis - 10) / stateObs.getAvailableActions.size)
      (action, evaluateStates(stateCopy, newTimer))
    }
    //    if (ended) {
    //      // we need to stop looking at it so the framework doesn't break.
    //      IndividualHolder.synchronized {
    //        IndividualHolder.currentState = null
    //      }
    //    }

    //    while (elapsedTimer.remainingTimeMillis > 10) {
    //
    //    }
    //    printf("Evaluated %d states\n", statesEvaluated)
    actionsScores.maxBy(actionScore => actionScore._2)._1
  }

  /**
   * recursively evaluates the followup states, until out of time.
   * @param stateObservation
   * @param elapsedCpuTimer the best (score, heuristic score) that can be reached from each state.
   */
  def evaluateStates(stateObservation: StateObservation, elapsedCpuTimer: ElapsedCpuTimer): Tuple2[Double, Double] = {
    if (elapsedCpuTimer.remainingTimeMillis() < 1 || stateObservation.isGameOver) {
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