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
import scala.collection.{Map, mutable}
import scalaj.collection.Imports._
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
    IndividualHolder.synchronized {
      IndividualHolder.currentState = stateObs
      IndividualHolder.notifyAll()
    }

    heuristic = new GPHeuristic()
    heuristic.waitForFirstIndividual()

    if (heuristic.gpRun.fitnessCalculator.gen != 0)
      heuristic.gpRun.fitnessCalculator.skipCurrentGen()

    IndividualHolder.synchronized {
      //      IndividualHolder.aStar.aStarCache.clear()
      val blockSize: Int = stateObs.getBlockSize
      val avatarPosition = stateObs.getAvatarPosition
      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)

      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
    }
    updateSpritesSet(stateObs)
    while (elapsedTimer.remainingTimeMillis() > 50) {}
  }

  val statesSelected = ListBuffer[ActionResult]()
  val timesTried = new ListBuffer[Int]()


  def act(stateObs: StateObservation, elapsedCpuTimer: ElapsedCpuTimer): Types.ACTIONS = {
    updateSpritesSet(stateObs)
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()


    while(elapsedCpuTimer.remainingTimeMillis() > 5) {
      
    }
  }


  def act1(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): Types.ACTIONS = {
    updateSpritesSet(stateObs)
    IndividualHolder.currentState = stateObs.copy
    heuristic.useBestKnownIndividual()
    statesEvaluated = 0
    val availableActions = stateObs.getAvailableActions(true).asScalaMutable
    val possibleResults = new mutable.HashMap[ACTIONS, ListBuffer[ActionResult]]()
    for (action <- availableActions) {
      possibleResults.put(action, new ListBuffer[ActionResult]())
    }
    var timesEvaluated = 0
    while (elapsedTimer.remainingTimeMillis() > 5
    //      elapsedTimer.elapsedMillis() / timesEvaluated > elapsedTimer.remainingTimeMillis()
    ) {
      for (action <- availableActions) {
        val stateCopy = stateObs.copy()
        stateCopy.advance(action)
        val currentResult = maxStateToDepth(heuristic.individual.get, action, stateCopy, 2, 1)
        possibleResults(action) += currentResult
      }
      timesEvaluated += 1
    }
    //    printf("%d\n", timesEvaluated)
    //    timesTried += timesEvaluated - 1

    //    if ((timesTried.length+1) % 100 == 0){
    //      println("*** Agent retried average " + timesTried.sum / (timesTried.length + 1))
    //      timesTried.clear()
    //    }


    val actionScores: Map[ACTIONS, Double] = possibleResults.mapValues(results => results.map(_.heuristicScore).sum)
    val actionGameScores: Map[ACTIONS, Double] = possibleResults.mapValues(results => results.map(_.gameScore).min)
    val bestResult: (ACTIONS, Double) = actionScores.toList.maxBy((f) => f._2)
    print(actionGameScores)
    println(bestResult._1)
    bestResult._1

    //    val possibleResults: Seq[ActionResult] = for (action <- availableActions) yield {
    //      val actionResults = for (i <- 0 to 3) yield {
    //        val stateCopy = stateObs.copy()
    //        stateCopy.advance(action)
    //        maxStateToDepth(heuristic.individual.get, action, stateCopy, 2, 1)
    //      }
    //      new ActionResult(action, actionResults.map(_.gameScore).sum, actionResults.map(_.heuristicScore).sum, 1, null)
    //    }

    //    possibleResults.maxBy(r => r.heuristicScore).action
  }

  def updateSpritesSet(stateObs: StateObservation) = {
    val newSpriteSet = new mutable.HashSet[String]()
    val allSprites = List(stateObs.getImmovablePositions, stateObs.getMovablePositions(), stateObs.getNPCPositions, stateObs.getPortalsPositions, stateObs.getResourcesPositions)
    for (observations <- allSprites) {
      if (observations != null)
        for (spritesList <- observations) {
          if (!spritesList.isEmpty)
            newSpriteSet.add(spritesList.get(0).itype.toString)
        }
    }
    IndividualHolder.valuesList += (("sprites", newSpriteSet.toSeq))
  }
}


