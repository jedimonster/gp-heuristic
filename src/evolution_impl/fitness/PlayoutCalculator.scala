package evolution_impl.fitness

import java.util

import core.game.StateObservation
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.{HeuristicIndividual, JavaCodeIndividual}
import evolution_impl.search.Position
import ontology.Types
import ontology.Types.{WINNER, ACTIONS}
import tools.ElapsedCpuTimer
import scala.annotation.tailrec
import scalaj.collection.Imports._
import scala.collection.JavaConversions._


/**
 * Created by itayaza on 03/02/2015.
 */
trait PlayoutCalculator {

  protected val Gamma: Double = 0.99

  def playout(individual: HeuristicIndividual, stateObservation: StateObservation): (Double, Double, Int) = {
    adjustableWidthPlayout(individual, stateObservation, 2, 1, 70)
  }

//  /**
//   * Simply picks the next move according to best heuristic value all the way down the tree or cutoff.
//   * @param individual
//   * @param stateObservation
//   * @param cutoff
//   * @return Double - score of the best state encountered during the playout.
//   */
//  def playout(individual: JavaCodeIndividual, stateObservation: StateObservation, cutoff: Int): Double = {
//    // todo this returns best state score, so if we're one step from losing, we'll return the winning score!
//    var state = stateObservation.copy
//    var bestState = state
//    var bestHeuristicScore = Double.MinValue
//    var bestStateScore = 0.0
//
//    do {
//      bestHeuristicScore = Double.MinValue
//
//      for (action <- state.getAvailableActions) {
//        var stateCopy: StateObservation = state.copy()
//        stateCopy.advance(action)
//        val score: Double = stateCopy.getGameScore
//        var heuristicVal = individual.run(new StateObservationWrapper(stateCopy))
//
//        if (stateCopy.getAvatarOrientation.eq(stateObservation.getAvatarOrientation)) {
//          //           we only turned so it had no effect, move so it does and heuristics can update
//          val moveTwiceState = stateCopy.copy()
//          moveTwiceState.advance(action)
//          val moveTwiceHeuristicVal = individual.run(new StateObservationWrapper(moveTwiceState))
//          if (moveTwiceHeuristicVal > heuristicVal) {
//            heuristicVal = moveTwiceHeuristicVal
//            stateCopy = moveTwiceState
//          }
//          //          stateCopy.advance(action)
//          //          heuristicVal = individual.run(new StateObservationWrapper(stateCopy))
//
//        }
//
//        if (heuristicVal > bestHeuristicScore) {
//          bestHeuristicScore = heuristicVal
//          bestState = stateCopy
//        }
//        if (score > bestStateScore)
//          bestStateScore = score
//      }
//
//      state = bestState
//    }
//
//    while (state.getGameTick < cutoff && !state.isGameOver)
//
//    bestStateScore
//  }

  protected val heuristicWeight: Double = 1.0

  @tailrec final def recPlayout(individual: HeuristicIndividual, stateObservation: StateObservation, leadingAction: ACTIONS, maxDepth: Int, depthReached: Int = 0): ActionResult = {
    if (depthReached > maxDepth || stateObservation.isGameOver) {
      val heuristicVal: Double = individual.run(new StateObservationWrapper(stateObservation))
      var gameScore: Double = stateObservation.getGameScore

      if (depthReached >= maxDepth || stateObservation.isGameOver) {

        if (stateObservation.getGameWinner == Types.WINNER.PLAYER_WINS)
          gameScore = 10 * gameScore
        else if (stateObservation.getGameWinner == Types.WINNER.PLAYER_LOSES)
          gameScore = -1 / Math.max(0.001, Math.abs(gameScore))
      }
      return new ActionResult(Some(leadingAction), gameScore, heuristicVal, depthReached, stateObservation)
    }

    val scores = for (nextAction <- stateObservation.getAvailableActions(false).asScala) yield {
      var nextState = stateObservation.copy()
      nextState.advance(nextAction)

      var heuristicScore: Double = individual.run(new StateObservationWrapper(nextState, IndividualHolder.aStar))
      // handle the case we changed orientation and didn't move by taking max(move, don't move)
      if (nextState.getAvatarPosition.equals(stateObservation.getAvatarPosition)) {
        val moveTwiceState = nextState.copy()
        moveTwiceState.advance(nextAction)
        val moveTwiceHeuristicScore = individual.run(new StateObservationWrapper(moveTwiceState, IndividualHolder.aStar))
        if (moveTwiceHeuristicScore > heuristicScore) {
          heuristicScore = moveTwiceHeuristicScore
          nextState = moveTwiceState
        }
      }


      val gameScore = nextState.getGameScore
      val weightedScore = heuristicWeight * heuristicScore + (1 - heuristicWeight) * gameScore
      (nextState, weightedScore)
    }
    // at this point we can follow up the best heuristic value, or  - if time permits - more.
    val bestScore = scores.maxBy(x => x._2)

    recPlayout(individual, bestScore._1, leadingAction, maxDepth, depthReached + 1)
  }

  /** *
    *
    * @param individual
    * @param stateObservation
    * @return (Score, Heuristic Val, depth_reached) best values that can be reached from the given state.
    */
  @tailrec final def rec_playout(individual: HeuristicIndividual, stateObservation: StateObservation, timeLeft: ElapsedCpuTimer, depthReached: Int = 0, maxDepth: Int = 70):
  (Double, Double, Int) = {
    if (depthReached > maxDepth || stateObservation.isGameOver) {
      val heuristicVal: Double = individual.run(new StateObservationWrapper(stateObservation))
      val gameScore: Double = stateObservation.getGameScore

      if (stateObservation.getGameWinner == Types.WINNER.PLAYER_WINS)
        return (10 * gameScore, heuristicVal, depthReached)
      else if (!stateObservation.isGameOver)
        return (gameScore, heuristicVal, depthReached)
      else
        return (-1 / Math.max(0.001, Math.abs(gameScore)), heuristicVal, depthReached)
    }

    val scores = for (nextAction <- stateObservation.getAvailableActions(false).asScala) yield {
      var nextState = stateObservation.copy()
      nextState.advance(nextAction)

      var heuristicScore: Double = individual.run(new StateObservationWrapper(nextState, IndividualHolder.aStar))
      // handle the case we changed orientation and didn't move by taking max(move, don't move)
      if (nextState.getAvatarPosition.equals(stateObservation.getAvatarPosition)) {
        val moveTwiceState = nextState.copy()
        moveTwiceState.advance(nextAction)
        val moveTwiceHeuristicScore = individual.run(new StateObservationWrapper(moveTwiceState, IndividualHolder.aStar))
        if (moveTwiceHeuristicScore > heuristicScore) {
          heuristicScore = moveTwiceHeuristicScore
          nextState = moveTwiceState
        }
      }


      val gameScore = nextState.getGameScore
      val weightedScore = heuristicWeight * heuristicScore + (1 - heuristicWeight) * gameScore
      (nextState, weightedScore)
    }
    // at this point we can follow up the best heuristic value, or  - if time permits - more.
    val bestScore = scores.maxBy(x => x._2)
    rec_playout(individual, bestScore._1, timeLeft, depthReached + 1, maxDepth)
  }

  //  def widePlayout(individual: HeuristicIndividual, stateObservation: StateObservation, timeLeft: ElapsedCpuTimer, depth: Int):
  //  (Double, Double, Int) = {
  //    val stateToPlayout = maxStateToDepth(individual, stateObservation, depth)._1
  //    rec_playout(individual, stateToPlayout, timeLeft, depth)
  //  }

  //  def maxStateToDepth(individual: HeuristicIndividual, stateObservation: StateObservation, depth: Int):
  //  (StateObservation, Double) = {
  //    if (depth == 0 || stateObservation.isGameOver) {
  //      (stateObservation, individual.run(new StateObservationWrapper(stateObservation, IndividualHolder.aStar)))
  //    } else {
  //      val successorsValues = for (action <- stateObservation.getAvailableActions(false).asScalaMutable)
  //        yield {
  //          val stateCopy: StateObservation = stateObservation.copy()
  //          stateCopy.advance(action)
  //          maxStateToDepth(individual, stateCopy, depth - 1)
  //        }
  //      successorsValues.maxBy((sv: (StateObservation, Double)) => sv._2)
  //    }
  //  }

  def adjustableWidthPlayout(individual: HeuristicIndividual, stateObservation: StateObservation, maxDepth: Int, iterations: Int, oneStepDepth: Int = 70):
  (Double, Double, Int) = {
    var currentBestState: StateObservation = stateObservation
    var action: Option[ACTIONS] = None
    for (i <- 0 to iterations - 1) {
      val bestResult = maxStateToDepth(individual, action, currentBestState, maxDepth)
      currentBestState = bestResult.stateObservation
      action = bestResult.action
    }
    rec_playout(individual, currentBestState, new ElapsedCpuTimer(), 0, oneStepDepth)
  }

  def maxStateToDepth(heuristic: HeuristicIndividual, originalAction: Option[ACTIONS], stateObservation: StateObservation, maxDepth: Int = 2, depth: Int = 0): ActionResult = {
    //    if (elapsedCpuTimer.remainingTimeMillis() <= heuristicEvalTime * stateObservation.getAvailableActions.size || stateObservation.isGameOver) {
    if (depth >= maxDepth || stateObservation.isGameOver) {
      val heuristicScore = heuristic.run(new StateObservationWrapper(stateObservation))

      val score: Double = stateObservation.getGameScore
      if (stateObservation.getGameWinner == WINNER.PLAYER_WINS)
        return new ActionResult(originalAction, Math.pow(Gamma, depth) * Double.MaxValue, Math.pow(Gamma, depth) * Double.MaxValue, depth, stateObservation)
      else if (stateObservation.getGameWinner == WINNER.PLAYER_LOSES || stateObservation.getGameWinner == WINNER.PLAYER_DISQ)
        return new ActionResult(originalAction, Double.MinValue, Double.MinValue, depth, stateObservation)
      return new ActionResult(originalAction, score, Math.pow(Gamma, depth) * heuristicScore, depth + 1, stateObservation)
    }
    val availableActions: util.ArrayList[ACTIONS] = stateObservation.getAvailableActions(false)

    val possible_scores = for (action <- availableActions) yield {
      var stateCopy: StateObservation = stateObservation.copy

      val leadingAction = originalAction match {
        case Some(a) => Some(a)
        case None => Some(action)
      }
      stateCopy.advance(action)

      //      if (stateCopy.getAvatarPosition equals stateObservation.getAvatarPosition) {
      //        val moveTwiceState = stateCopy.copy()
      //        moveTwiceState.advance(action)
      //        if (heuristic.run(new StateObservationWrapper(moveTwiceState)) > heuristic.run(new StateObservationWrapper(stateCopy)))
      //          stateCopy = moveTwiceState
      //      }

      val actionResult: ActionResult = maxStateToDepth(heuristic, leadingAction, stateCopy, maxDepth, depth + 1)
      actionResult
    }
    val childrenMax = possible_scores.maxBy(actionResult => actionResult.heuristicScore * heuristicWeight + actionResult.gameScore * (1 - heuristicWeight))

    //    val stateHeuristicVal: Double = Math.pow(Gamma, depth) * heuristic.run(new StateObservationWrapper(stateObservation))
    //    new ActionResult(childrenMax.action, childrenMax.gameScore, childrenMax.heuristicScore + stateHeuristicVal, childrenMax.depth, childrenMax.stateObservation)

    //    if (stateHeuristicVal >= childrenMax.heuristicScore)
    //      if (stateHeuristicVal >= childrenMax.heuristicScore && childrenMax.heuristicScore > Double.MinValue / 10)
    //        return new ActionResult(originalAction, stateObservation.getGameScore, stateHeuristicVal, depth, stateObservation)
    //        return new ActionResult(originalAction, childrenMax.gameScore, stateHeuristicVal + childrenMax.heuristicScore, depth)

    childrenMax
  }
}


class ActionResult(val action: Option[ACTIONS], val gameScore: Double, val heuristicScore: Double, val depth: Int, val stateObservation: StateObservation) {

  override def toString = s"ActionResult(originalAction=$action, gameScore=$gameScore, heuristicScore=$heuristicScore, depth=$depth)"
}