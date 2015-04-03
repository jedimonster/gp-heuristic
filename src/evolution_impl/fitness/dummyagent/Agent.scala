package evolution_impl.fitness.dummyagent


import controllers.Heuristics.SimpleStateHeuristic
import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{IndividualHolder, PlayoutCalculator}
import ontology.Types
import ontology.Types.ACTIONS
import tools.ElapsedCpuTimer
import java.util.concurrent.TimeoutException
import scala.collection.JavaConversions._

class Agent extends AbstractPlayer with PlayoutCalculator {
  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
  }

  /**
   *
   * Very simple one step lookahead agent.
   *
   * @param stateObs Observation of the current state.
   * @param elapsedTimer Timer when the action returned is due.
   * @return An action for the current state
   */
  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    // todo broken due to threads; we can't use CurentIndividualHolder
    var bestAction: Types.ACTIONS = null
    var maxQ: Double = Double.MinValue
    val heuristic = new GPHeuristic()
    val actions: java.util.ArrayList[ACTIONS] = stateObs.getAvailableActions

    for (action: ACTIONS <- actions.toSet) {
      val stCopy: StateObservation = stateObs.copy
      stCopy.advance(action)
      if (stCopy.getAvatarOrientation.eq(stateObs.getAvatarOrientation)) {
        // we only turned so it had no effect, move so it does and heuristics can update
        // todo we might not want to move but only look at something.. but we really need to look past it, pick max, and follow through.
        stCopy.advance(action)
      }
      val Q: Double = heuristic.evaluateState(stCopy)
      if (Q > maxQ) {
        maxQ = Q
        bestAction = action
      }
    }
    bestAction
  }
}