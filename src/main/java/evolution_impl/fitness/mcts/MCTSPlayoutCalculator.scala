package evolution_impl.fitness.mcts

import java.util

import core.game.StateObservation
import evolution_impl.fitness.PlayoutCalculator
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.HeuristicIndividual
import ontology.Types.{WINNER, ACTIONS}
import tools.Utils

import scala.collection.mutable
import scala.util.Random

/**
  * Created by Itay on 17-Feb-16.
  */
trait MCTSPlayoutCalculator extends PlayoutCalculator {
  protected val stateHeuristicScoreMap = new mutable.HashMap[HeuristicNode, Double]()
  final val Epsilon = 1e-6
  final val C = Math.sqrt(2)
//  final val C = 0.1

  var maxHeuristicScore = Double.MinValue
  var minHeuristicScore = Double.MaxValue


  //  override def adjustableWidthPlayout(individual: HeuristicIndividual, stateObservation: StateObservation, maxDepth: Int, iterations: Int): (Double, Double, Int) = {
  //
  //    ???
  //  }

  def mcts(individual: HeuristicIndividual, stateObservation: StateObservation, maxDepth: Int, iterations: Int): HeuristicNode = {
    val heuristicNode = new HeuristicNode(stateObservation)
    for (i <- 0 to iterations)
      mctsPlayout(heuristicNode, individual, maxDepth)

    heuristicNode
  }

  def mctsPlayout(root: HeuristicNode, heuristicIndividual: HeuristicIndividual, maxDepth: Int) = {
    val selectedNode = selectNode(root, maxDepth)
    val heuristicResult = simulateRollout(selectedNode, heuristicIndividual, maxDepth)
    selectedNode.backpropogate(heuristicResult)
  }

  /**
    * traverse down the tree (using uct) until we meet a barren node, then return it.
    * limited by maxDepth
    *
    * @param root
    * @return
    */
  def selectNode(root: HeuristicNode, maxDepth: Int): HeuristicNode = {
    var depth = 0
    var currentNode = root

    while (!currentNode.state.isGameOver && depth < maxDepth) {
      if (!currentNode.isFullyExpanded)
        return expand(currentNode)

      currentNode = selectChild(currentNode)
      depth += 1
    }
    currentNode
  }

  /**
    * selects child to explore, currently using UCB
    *
    * @param heuristicNode
    * @return
    */
  def selectChild(heuristicNode: HeuristicNode): HeuristicNode = {
    val n = heuristicNode.heuristicScores.size + 1
    var bestUCB = Double.MinValue
    var selectedNode: Option[HeuristicNode] = None

    val childUCB = for (child <- heuristicNode.children) yield {
      val childN: Double = child.heuristicScores.size + Epsilon
      var childValue = child.heuristicScores.sum / childN
      childValue = Utils.normalise(childValue, minHeuristicScore, maxHeuristicScore)
      val ucbValue = childValue + C * Math.sqrt(Math.log(n) / childN)

      //      if (ucbValue >= bestUCB) {
      //        bestUCB = ucbValue
      //        selectedNode = Some(child)
      //      }
      (child, ucbValue)
    }

    //    selectedNode match {
    //      case Some(child) => child
    //      case None => throw new RuntimeException("This shouldn't happen")
    //    }
    childUCB.maxBy(_._2)._1
  }

  def expand(heuristicNode: HeuristicNode): HeuristicNode = {
    var bestAction = 0
    var bestValue = Double.MinValue

    for (i <- heuristicNode.children.indices) {
      val childValue = Random.nextDouble()
      if (childValue > bestValue && heuristicNode.children(i) == null) {
        bestAction = i
        bestValue = childValue
      }
    }

    val nextState = heuristicNode.state.copy
    nextState.advance(nextState.getAvailableActions.get(bestAction))

    val nextNode = new HeuristicNode(nextState, Some(heuristicNode))
    heuristicNode.children(bestAction) = nextNode

    nextNode
  }

  val HugeNumber: Int = Int.MaxValue / 10

  def simulateRollout(heuristicNode: HeuristicNode, heuristicIndividual: HeuristicIndividual, maxDepth: Int): Double = {
    var rollingState = heuristicNode.state.copy
    val actions: util.ArrayList[ACTIONS] = rollingState.getAvailableActions
    var remainingDepth = maxDepth

    while (!rollingState.isGameOver && remainingDepth > 0) {
      val action = actions.get(Random.nextInt(actions.size))
      rollingState.advance(action)

      remainingDepth -= 1
    }


    var heuristicScore = heuristicIndividual.run(new StateObservationWrapper(rollingState))
    maxHeuristicScore = Math.max(maxHeuristicScore, heuristicScore)
    minHeuristicScore = Math.min(minHeuristicScore, heuristicScore)

    if (rollingState.isGameOver && rollingState.getGameWinner != WINNER.PLAYER_WINS)
      heuristicScore = -HugeNumber

    heuristicScore
  }
}
