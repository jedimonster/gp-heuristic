package controllers.heauristicGP

import controllers.Heuristics.StateHeuristic
import core.game.StateObservation

/**
 * Created By Itay Azaria
 * Date: 4/7/2015
 */
class ScoreHeuristic extends StateHeuristic{
  override def evaluateState(stateObs: StateObservation): Double = stateObs.getGameScore
}
