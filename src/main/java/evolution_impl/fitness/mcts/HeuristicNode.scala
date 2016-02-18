package evolution_impl.fitness.mcts

import core.game.StateObservation
import ontology.Types.ACTIONS

import scala.collection.mutable.ListBuffer

/**
  * Created by Itay on 17-Feb-16.
  */
class HeuristicNode(
                     stateObservation: StateObservation,
                     parent: Option[HeuristicNode] = None) {
  def backpropogate(heuristicResult: Double): Unit = {
    heuristicScores += heuristicResult
    if (parent.isDefined)
      parent.get.backpropogate(heuristicResult)
  }


  val state = stateObservation
  val children = new Array[HeuristicNode](stateObservation.getAvailableActions.size)
  var heuristicScores = new ListBuffer[Double]()

  def isFullyExpanded = !children.contains(null)
}
