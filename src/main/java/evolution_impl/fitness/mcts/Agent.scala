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
  override def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): ACTIONS = {
    updateSpritesSet(stateObs)
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()

    //    val root: HeuristicNode = mcts(heuristic.individual.get, stateObs, 15, 20)
    val root = new HeuristicNode(stateObs)

    while (elapsedTimer.remainingTimeMillis() > 0)
      mctsPlayout(root, heuristic.individual.get, 10)

    var mostVisits = Double.MinValue
    var action = -1
    for (i <- root.children.indices) {
      val childVisits = {
        if (root.children(i) == null || root.children(i).heuristicScores.isEmpty)
          0 // todo why can this happen?
        else
          root.children(i).heuristicScores.sum / root.children(i).heuristicScores.length
      }

      if (childVisits >= mostVisits) {
        mostVisits = childVisits
        action = i
      }
    }

    stateObs.getAvailableActions.get(action)

  }
}
