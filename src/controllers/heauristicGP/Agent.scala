package controllers.heauristicGP

import java.util

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.IndividualHolder
import evolution_impl.search.{Position, GraphCachingAStar}
import ontology.Types
import ontology.Types.{WINNER, ACTIONS}
import tools.ElapsedCpuTimer

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
class Agent extends AbstractPlayer {
  protected var heuristic: GPHeuristic = null
  protected var statesEvaluated = 0
  protected var statesEvaluatedCounts = ListBuffer[Int]()
  protected var actions = 0
  protected var heuristicEvalTime = 0.0


  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
    heuristic = new GPHeuristic()
    heuristic.waitForFirstIndividual()


    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      //      IndividualHolder.aStar.aStarCache.clear()
      val blockSize: Int = stateObs.getBlockSize
      val avatarPosition = stateObs.getAvatarPosition
      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)

      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
  }

  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
    //    heuristic.individual = IndividualHolder.bestIndividual
    statesEvaluated = 0

    // estimate amount of time available for each heuristic eval by evaluating the current state.
    heuristic.evaluateState(stateObs)
    val timer = new ElapsedCpuTimer()
    heuristic.evaluateState(stateObs) // we do this twice because the 2nd time closer to the average
    heuristicEvalTime = timer.elapsedMillis()
    val newTimer = new ElapsedCpuTimer()
    val remainingTime: Long = (elapsedTimer.remainingTimeMillis() * 0.8).asInstanceOf[Long]
    newTimer.setMaxTimeMillis(remainingTime)

    val actionScores = evaluateStates(ACTIONS.ACTION_NIL, stateObs, newTimer)

    //    val actionsScores = for (action <- stateObs.getAvailableActions) yield {
    //      val stateCopy: StateObservation = stateObs.copy
    //      stateCopy.advance(action)
    //      val newTimer = new ElapsedCpuTimer()
    //      newTimer.setMaxTimeMillis((elapsedTimer.remainingTimeMillis - 10) / stateObs.getAvailableActions.size)
    //      (action, evaluateStates(action, stateCopy, newTimer))
    //    }
    statesEvaluatedCounts :+= statesEvaluated
    actions += 1
    //
    if (actions % 100 == 0) {
      printf("Average states evaluated by agent %f\n", statesEvaluatedCounts.sum.toDouble / statesEvaluatedCounts.size)
      statesEvaluatedCounts = ListBuffer[Int]()
    }
    //    actionsScores.maxBy(actionScore => actionScore._2)._1


    actionScores.action

  }

  val heuristicWeight: Double = 1.0

  protected val Gamma: Double = 0.99

  /**
   * recursively evaluates the followup states, until out of time.
   * @param stateObservation
   * @param elapsedCpuTimer the best (score, heuristic score) that can be reached from each state.
   */
  def evaluateStates(originalAction: ACTIONS, stateObservation: StateObservation, elapsedCpuTimer: ElapsedCpuTimer, depth: Int = 0): ActionResult = {
    //    if (elapsedCpuTimer.remainingTimeMillis() <= heuristicEvalTime * stateObservation.getAvailableActions.size || stateObservation.isGameOver) {
    if (depth >= 2 || stateObservation.isGameOver) {
      statesEvaluated += 1
      val score: Double = stateObservation.getGameScore

      if (stateObservation.getGameWinner == WINNER.PLAYER_WINS)
        return new ActionResult(originalAction, Math.pow(Gamma, depth) * Double.MaxValue, Math.pow(Gamma, depth) * Double.MaxValue, depth)
      if (stateObservation.isGameOver)
        return new ActionResult(originalAction, Double.MinValue, Double.MinValue, depth)
      return new ActionResult(originalAction, score, Math.pow(Gamma, depth) * heuristic.evaluateState(stateObservation), depth + 1)
    }
    val availableActions: util.ArrayList[ACTIONS] = stateObservation.getAvailableActions(false)
    val estimatedTimePerAction: Long = elapsedCpuTimer.remainingTimeMillis / availableActions.size

    val possible_scores = for (action <- availableActions) yield {
      var stateCopy: StateObservation = stateObservation.copy
      val newTimer = new ElapsedCpuTimer()

      stateCopy.advance(action)
      if (stateCopy.getAvatarPosition equals stateObservation.getAvatarPosition) {
        val moveTwiceState = stateCopy.copy()
        moveTwiceState.advance(action)
        if (heuristic.evaluateState(moveTwiceState) > heuristic.evaluateState(stateCopy))
          stateCopy = moveTwiceState
      }
      newTimer.setMaxTimeMillis(estimatedTimePerAction)
      val actionResult: ActionResult = evaluateStates(action, stateCopy, newTimer, depth + 1)
      new ActionResult(action, actionResult.gameScore, actionResult.heuristicScore, actionResult.depth)
    }
    val childrensMax = possible_scores.maxBy(actionResult => actionResult.heuristicScore * heuristicWeight + actionResult.gameScore * (1 - heuristicWeight))
    val stateHeuristicVal: Double = Math.pow(Gamma, depth) * heuristic.evaluateState(stateObservation)

//        if (stateHeuristicVal >= childrensMax.heuristicScore)
    if (stateHeuristicVal >= childrensMax.heuristicScore && childrensMax.heuristicScore > Double.MinValue / 10)
      return new ActionResult(originalAction, stateObservation.getGameScore, stateHeuristicVal, depth)
//    return new ActionResult(originalAction, childrensMax.gameScore, stateHeuristicVal + childrensMax.heuristicScore, depth)
    childrensMax
  }
}

class ActionResult(val action: ACTIONS, val gameScore: Double, val heuristicScore: Double, val depth: Int) {

  override def toString = s"ActionResult(originalAction=$action, gameScore=$gameScore, heuristicScore=$heuristicScore, depth=$depth)"
}