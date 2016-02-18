package evolution_impl.fitness.mcts

import core.game.StateObservation
import ontology.Types.ACTIONS

import scala.collection.mutable.ListBuffer

/**
  * Created by Itay on 17-Feb-16.
  */
class HeuristicNode(stateObservation: StateObservation) {
  val heuristicScores = new scala.collection.mutable.HashMap[ACTIONS, Double]()


}
