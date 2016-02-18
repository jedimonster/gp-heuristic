package evolution_impl.fitness.mcts

import core.game.StateObservation
import evolution_impl.fitness.PlayoutCalculator
import evolution_impl.gpprograms.base.HeuristicIndividual

import scala.collection.mutable
import scala.util.Random

/**
  * Created by Itay on 17-Feb-16.
  */
class MCTSPlayoutCalculator extends PlayoutCalculator {
  protected val stateHeuristicScoreMap = new mutable.HashMap[HeuristicNode, Double]()

  override def adjustableWidthPlayout(individual: HeuristicIndividual, stateObservation: StateObservation, maxDepth: Int, iterations: Int): (Double, Double, Int) = {
    ???
  }

  def mctsPlayout(root: HeuristicNode, maxDepth: Int) = {
    var selectedNode = select(root)
    val actions = root.stateObservation
    val action = Random.nextInt()
  }

  def select(heuristicNode: HeuristicNode): HeuristicNode = ???

  def expand(heuristicNode: HeuristicNode): HeuristicNode = ???

  def simulatePlayout(heuristicNode: HeuristicNode, remainingDepth: Int) = ???
}
