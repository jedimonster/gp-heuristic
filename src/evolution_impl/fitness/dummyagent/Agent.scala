package evolution_impl.fitness.dummyagent


import controllers.Heuristics.SimpleStateHeuristic
import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{PlayoutCalculator, CurrentIndividualHolder}
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
    var bestAction: Types.ACTIONS = null
    var maxQ: Double = Double.MinValue
    val heuristic = new GPHeuristic(CurrentIndividualHolder.individual)
    val actions: java.util.ArrayList[ACTIONS] = stateObs.getAvailableActions
    for (action: ACTIONS <- actions.toSet) {
      val stCopy: StateObservation = stateObs.copy
      stCopy.advance(action)
      val Q: Double = heuristic.evaluateState(stCopy)
      if (Q > maxQ) {
        maxQ = Q
        bestAction = action
      }
    }
    bestAction
  }
}