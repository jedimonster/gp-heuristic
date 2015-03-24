package controllers.heauristicGP

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{IndividualHolder, IndividualHolder$}
import ontology.Types
import tools.ElapsedCpuTimer
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

  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
    heuristic = new GPHeuristic(null)
    heuristic.waitForFirstIndividual()

    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
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
  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    var bestAction: Types.ACTIONS = null
    var maxQ: Double = Double.NegativeInfinity

    IndividualHolder.currentState = stateObs.copy

    for (action <- stateObs.getAvailableActions) {
      val stCopy: StateObservation = stateObs.copy
      stCopy.advance(action)
      val Q: Double = heuristic.evaluateState(stCopy)
      if (Q > maxQ) {
        maxQ = Q
        bestAction = action
      }
    }
    while (elapsedTimer.remainingTimeMillis > 10) {
    }
    return bestAction
  }
}