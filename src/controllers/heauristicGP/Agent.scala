package controllers.heauristicGP

import java.util

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{ActionResult, PlayoutCalculator, IndividualHolder}
import evolution_impl.search.{Position, GraphCachingAStar}
import ontology.Types
import ontology.Types.{WINNER, ACTIONS}
import tools.ElapsedCpuTimer

import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
class Agent extends AbstractPlayer with PlayoutCalculator {
  protected var heuristic: GPHeuristic = null
  protected var statesEvaluated = 0
  protected var statesEvaluatedCounts = ListBuffer[Int]()
  protected var actions = 0
  protected var heuristicEvalTime = 0.0


  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
    this()
    heuristic = new GPHeuristic()
    heuristic.waitForFirstIndividual()

    if (heuristic.gpRun.fitnessCalculator.gen != 0)
      heuristic.gpRun.fitnessCalculator.skipCurrentGen()

    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      //      IndividualHolder.aStar.aStarCache.clear()
      val blockSize: Int = stateObs.getBlockSize
      val avatarPosition = stateObs.getAvatarPosition
      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)

      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
    //    while (elapsedTimer.remainingTimeMillis() > 50) {}
  }

  val statesSelected = ListBuffer[ActionResult]()

  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
    statesEvaluated = 0

    val maxStates = for (i <- 0 to 1) yield maxStateToDepth(heuristic.individual.get, ACTIONS.ACTION_NIL, stateObs, 2)
    val maxState = maxStates.maxBy(s => s.heuristicScore)
    //    val maxState = maxStateToDepth(heuristic.individual.get, ACTIONS.ACTION_NIL, stateObs, 2)
    //    statesSelected += maxState
    while (elapsedTimer.remainingTimeMillis() > 11) {
    }
    maxState.action
  }
}


