package evolution_impl.fitness

import core.game.StateObservation
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.JavaCodeIndividual
import scalaj.collection.Imports._

/**
 * Created by itayaza on 03/02/2015.
 */
trait PlayoutCalculator {
  def playout(individual: JavaCodeIndividual, stateObservation: StateObservation, cutoff: Int): Double = {
    var state = stateObservation.copy
    var bestState = state
    var bestStateScore = Double.MinValue

    do {
      bestStateScore = Double.MinValue

      for (action <- state.getAvailableActions) {
        val stateCopy: StateObservation = state.copy()
        stateCopy.advance(action)

        if (stateCopy.getGameScore > bestStateScore) {
          bestStateScore = stateCopy.getGameScore
          bestState = stateCopy
        }
      }

      state = bestState
    } while (state.getGameTick < cutoff && !state.isGameOver)

    bestStateScore
  }
}
