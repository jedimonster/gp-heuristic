package controllers.heauristicGP

import java.util

import core.game.StateObservation
import core.player.AbstractPlayer
import evolution_impl.GPHeuristic
import evolution_impl.fitness.{PlayoutCalculator, IndividualHolder}
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


    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      //      IndividualHolder.aStar.aStarCache.clear()
      val blockSize: Int = stateObs.getBlockSize
      val avatarPosition = stateObs.getAvatarPosition
      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)

      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
    while (elapsedTimer.remainingTimeMillis() > 50) {}
  }

  def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
    //    heuristic.individual = IndividualHolder.bestIndividual
    statesEvaluated = 0

    // estimate amount of time available for each heuristic eval by evaluating the current state.
    //    heuristic.evaluateState(stateObs)
    //    val timer = new ElapsedCpuTimer()
    //    heuristic.evaluateState(stateObs) // we do this twice because the 2nd time closer to the average
    //    heuristicEvalTime = timer.elapsedMillis()
    //    val newTimer = new ElapsedCpuTimer()
    //    val remainingTime: Long = (elapsedTimer.remainingTimeMillis() * 0.8).asInstanceOf[Long]
    //    newTimer.setMaxTimeMillis(remainingTime)

    //    val actionScores = evaluateStates(ACTIONS.ACTION_NIL, stateObs, newTimer)
    val maxState = maxStateToDepth(heuristic.individual.get, ACTIONS.ACTION_NIL, stateObs, 2)
    //    while (elapsedTimer.remainingTimeMillis() > 20) {}
    while (elapsedTimer.remainingTimeMillis() > 11) {

    }
    maxState.action
    //    val actionsScores = for (action <- stateObs.getAvailableActions) yield {
    //      val stateCopy: StateObservation = stateObs.copy
    //      stateCopy.advance(action)
    //      val newTimer = new ElapsedCpuTimer()
    //      newTimer.setMaxTimeMillis((elapsedTimer.remainingTimeMillis - 10) / stateObs.getAvailableActions.size)
    //      (action, evaluateStates(action, stateCopy, newTimer))
    //    }
    //    statesEvaluatedCounts :+= statesEvaluated
    //    actions += 1
    //
    //    if (actions % 100 == 0) {
    //      printf("Average states evaluated by agent %f\n", statesEvaluatedCounts.sum.toDouble / statesEvaluatedCounts.size)
    //      statesEvaluatedCounts = ListBuffer[Int]()
    //    }
    //    actionsScores.maxBy(actionScore => actionScore._2)._1


    //    actionScores.action

  }
}


