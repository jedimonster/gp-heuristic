package controllers.heauristicGP

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{PlayoutCalculator, IndividualHolder}
import evolution_impl.gpprograms.base.HeuristicIndividual
import evolution_impl.search.{GraphCachingAStar, Position}
import ontology.Types.ACTIONS
import tests.MinDistanceToImmovableHeuristic
import tools.ElapsedCpuTimer

/**
 * Created By Itay Azaria
 * Date: 19/05/2015
 */
class StaticHeuristicAgent extends AbstractPlayer with PlayoutCalculator {
  val heuristic = new MinDistanceToImmovableHeuristic()

  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()

    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      val blockSize: Int = stateObs.getBlockSize
      val avatarPosition = stateObs.getAvatarPosition
      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)

      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
    while (elapsedTimer.remainingTimeMillis() > 50) {}
  }

  override def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): ACTIONS = {
    val maxState = maxStateToDepth(heuristic, ACTIONS.ACTION_NIL, stateObs, 2)
    //    statesSelected += maxState
    while (elapsedTimer.remainingTimeMillis() > 11) {
    }
    maxState.action
  }
}
