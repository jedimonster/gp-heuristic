package evolution_impl.fitness.playouts

import core.game.StateObservation
import evolution_impl.fitness.{ActionResult, PlayoutCalculator}
import evolution_impl.gpprograms.base.HeuristicIndividual
import ontology.Types.ACTIONS

/**
  * Created by Itay on 17-Feb-16.
  */
class RandomPlayoutCalculator extends PlayoutCalculator{
  override def maxStateToDepth(heuristic: HeuristicIndividual, originalAction: ACTIONS, stateObservation: StateObservation, maxDepth: Int, depth: Int): ActionResult = {
    ???
  }
}
