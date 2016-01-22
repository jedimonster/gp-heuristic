//package controllers.heauristicGP
//
//import core.game.StateObservation
//import core.player.AbstractPlayer
//import evolution_impl.GPHeuristic
//import evolution_impl.fitness.{ActionResult, PlayoutCalculator, IndividualHolder}
//import evolution_impl.gpprograms.base.HeuristicIndividual
//import evolution_impl.search.{GraphCachingAStar, Position}
//import ontology.Types.ACTIONS
//import tests.MinDistanceToImmovableHeuristic
//import tools.ElapsedCpuTimer
//
//import scala.collection.{Map, mutable}
//import scala.collection.mutable.ListBuffer
//import scalaj.collection.Imports._
//
///**
// * Created By Itay Azaria
// * Date: 19/05/2015
// */
//class StaticHeuristicAgent extends AbstractPlayer with PlayoutCalculator {
//  val heuristic = new MinDistanceToImmovableHeuristic()
//
//  def this(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer) {
//    this()
//
//    IndividualHolder.synchronized {
//      IndividualHolder.currentState = stateObs
//      val blockSize: Int = stateObs.getBlockSize
//      val avatarPosition = stateObs.getAvatarPosition
//      val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)
//
//      IndividualHolder.aStar = new GraphCachingAStar[Position](graphRoot)
//      IndividualHolder.notifyAll() // wake up any threads waiting for a new state
//    }
//    while (elapsedTimer.remainingTimeMillis() > 50) {}
//  }
//
//  override def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): ACTIONS = {
////    val maxState = maxStateToDepth(heuristic, ACTIONS.ACTION_NIL, stateObs, 3)
////    //    statesSelected += maxState
//////    while (elapsedTimer.remainingTimeMillis() > 11) {
//////    }
////    maxState.action
//
//
//
//    val availableActions = stateObs.getAvailableActions(true).asScalaMutable
//    val possibleResults = new mutable.HashMap[ACTIONS, ListBuffer[ActionResult]]()
//    for (action <- availableActions) {
//      possibleResults.put(action, new ListBuffer[ActionResult]())
//    }
//    var timesEvaluated = 1
//    while (elapsedTimer.remainingTimeMillis() > 10
//    //      elapsedTimer.elapsedMillis() / timesEvaluated > elapsedTimer.remainingTimeMillis()
//    ) {
//      for (action <- availableActions) {
//        val stateCopy = stateObs.copy()
//        stateCopy.advance(action)
//        val currentResult = maxStateToDepth(heuristic, action, stateCopy, 2, 1)
//        possibleResults(action) += currentResult
//      }
//      timesEvaluated += 1
//    }
//    //    timesTried += timesEvaluated - 1
//
//    //    if ((timesTried.length+1) % 100 == 0){
//    //      println("*** Agent retried average " + timesTried.sum / (timesTried.length + 1))
//    //      timesTried.clear()
//    //    }
//
//
//    val actionScores: Map[ACTIONS, Double] = possibleResults.mapValues(results => results.map(_.heuristicScore).sum)
//    actionScores.toList.maxBy((f) => f._2)._1
//
//  }
//}
