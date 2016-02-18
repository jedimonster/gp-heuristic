package evolution_impl.fitness.mcts

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.fitness.{IndividualHolder, PlayoutCalculator}
import ontology.Types.ACTIONS
import tools.ElapsedCpuTimer

/**
  * Created by Itay on 18-Feb-16.
  */
class Agent(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) extends controllers.heauristicGP.Agent(stateObs, elapsedTimer) with MCTSPlayoutCalculator {
  //  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
  //    super(stateObs, elapsedTimer)
  //  }
  override def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): ACTIONS = {
    updateSpritesSet(stateObs)
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
    val root: HeuristicNode = mcts(heuristic.individual.get, stateObs, 20, 10)
    var mostVisits = 0
    var action = -1
    for (i <- root.children.indices) {
      val childVisits: Int = root.children(i).heuristicScores.length
      if (childVisits > mostVisits) {
        mostVisits = childVisits
        action = i
      }
    }

    stateObs.getAvailableActions.get(action)
  }
}
