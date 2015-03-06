package evolution_impl.fitness

import core.game.StateObservation
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.JavaCodeIndividual
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
        val heuristicVal = individual.run(new StateObservationWrapper(stateCopy))
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
}
