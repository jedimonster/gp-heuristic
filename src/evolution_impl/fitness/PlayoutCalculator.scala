package evolution_impl.fitness

import core.game.StateObservation
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.JavaCodeIndividual
import ontology.Types
import tools.ElapsedCpuTimer
import scala.annotation.tailrec
import scalaj.collection.Imports._

/**
 * Created by itayaza on 03/02/2015.
 */
trait PlayoutCalculator {
  /**
   * Simply picks the next move according to best heuristic value all the way down the tree or cutoff.
   * @param individual
   * @param stateObservation
   * @param cutoff
   * @return Double - score of the best state encountered during the playout.
   */
  def playout(individual: JavaCodeIndividual, stateObservation: StateObservation, cutoff: Int): Double = {
    // todo this returns best state score, so if we're one step from losing, we'll return the winning score!
    var state = stateObservation.copy
    var bestState = state
    var bestHeuristicScore = Double.MinValue
    var bestStateScore = 0.0

    do {
      bestHeuristicScore = Double.MinValue

      for (action <- state.getAvailableActions) {
        val stateCopy: StateObservation = state.copy()
        stateCopy.advance(action)
        val score: Double = stateCopy.getGameScore
        var heuristicVal = individual.run(new StateObservationWrapper(stateCopy))

        if (stateCopy.getAvatarOrientation.eq(stateObservation.getAvatarOrientation)) {
          // we only turned so it had no effect, move so it does and heuristics can update
          // todo we might not want to move but only look at something.. but we really need to look past it, pick max, and follow through.
          stateCopy.advance(action)
          heuristicVal = individual.run(new StateObservationWrapper(stateCopy))
        }

        if (heuristicVal > bestHeuristicScore) {
          bestHeuristicScore = heuristicVal
          bestState = stateCopy
        }
        if (score > bestStateScore)
          bestStateScore = score
      }

      state = bestState
    } while (state.getGameTick < cutoff && !state.isGameOver)

    bestStateScore
  }

  /** *
    *
    * @param individual
    * @param stateObservation
    * @return (Score, Heuristic Val, depth_reached) best values that can be reached from the given state.
    */
  @tailrec final def rec_playout(individual: JavaCodeIndividual, stateObservation: StateObservation, timeLeft: ElapsedCpuTimer, depthReached: Int = 0):
  (Double, Double, Int) = {
    if (timeLeft.exceededMaxTime() || stateObservation.isGameOver) {
      val heuristicVal: Double = individual.run(new StateObservationWrapper(stateObservation))
      val gameScore: Double = stateObservation.getGameScore
      if (stateObservation.getGameWinner == Types.WINNER.PLAYER_WINS)
        return (gameScore, heuristicVal, depthReached)
      else
        return (-1 / Math.max(0.001, Math.abs(gameScore)), heuristicVal, depthReached)
    }

    val scores = for (nextAction <- stateObservation.getAvailableActions.asScala) yield {
      val nextState = stateObservation.copy()
      nextState.advance(nextAction)
      (nextState, individual.run(new StateObservationWrapper(nextState)))
    }
    // at this point we can follow up the best heuristic value, or  - if time permits - more.
    val bestScore = scores.maxBy(x => x._2)
    rec_playout(individual, bestScore._1, timeLeft, depthReached + 1)
  }
}
