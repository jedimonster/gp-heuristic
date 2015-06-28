//package evolution_impl.fitness.dummyagent
//
//import core.game.Observation
//import core.game.StateObservation
//import evolution_impl.fitness.IndividualHolder
//import evolution_impl.fitness.IndividualHolder$
//import evolution_impl.search.AStar
//import evolution_impl.search.AStarException
//import evolution_impl.search.AStarPathRequest
//import evolution_impl.search.Position
//import tools.Vector2d
//import java.util
//import scala.collection.JavaConversions._
//
///**
// * Created by itayaza on 22/12/2014.
// */
//class StateObservationWrapper {
//  protected var aStar: AStar[Position] = null
//  protected var so: StateObservation = null
//
//  def this(so: StateObservation, aStar: AStar[Position]) {
//    this()
//    this.so = so
//    this.aStar = aStar
//  }
//
//  def this(so: StateObservation) {
//    this(so, IndividualHolder.aStar)
//  }
//
//  def getGameScore: Double = {
//    so.getGameScore
//  }
//
//  def getGameTick: Int = {
//    so.getGameTick
//  }
//
//  def getBlockSize: Int = {
//    so.getBlockSize
//  }
//
//  def getAvatarSpeed: Double = {
//    so.getAvatarSpeed
//  }
//
//  def getAvatarOrientation: Vector2d = {
//    so.getAvatarOrientation
//  }
//
//  def countTouchingNPCs: Double = {
//    val oneBlockSqDistance: Double = Math.pow(so.getBlockSize, 2)
//    val npcPositions: util.List[Observation] = flatObservations(so.getNPCPositions(so.getAvatarPosition))
//
//    npcPositions.count(o => o.sqDist <= oneBlockSqDistance)
//  }
//
//  def getResourcesCount: Double = {
//    val idCountMap: util.HashMap[Integer, Integer] = so.getAvatarResources
//    var sum: Double = 0
//
//    for (v <- idCountMap.values) {
//      sum += v
//    }
//    sum
//  }
//
//  def getNPCCount: Double = {
//    var sum: Double = 0
//    val npcPositions: Array[util.ArrayList[Observation]] = so.getNPCPositions
//    if (npcPositions != null) {
//      for (observations <- npcPositions) {
//        sum += observations.size
//      }
//    }
//    sum
//  }
//
//  def getImmovablePositions(@AllowedValues(values = Array("4")) category: Int, @AllowedValues(values = Array("2")) itype: Int): java.lang.Iterable[Observation] = {
//    val avatarPosition: Vector2d = so.getAvatarPosition
//    val immovablePositions: Array[util.ArrayList[Observation]] = so.getImmovablePositions(avatarPosition)
//    flatObservations(immovablePositions)
//  }
//
//  def getImmovableRealDistance: java.lang.Iterable[Double] = {
//    val npcPositions: Array[util.ArrayList[Observation]] = so.getImmovablePositions
//    getAStarDistances(npcPositions)
//  }
//
//  def getNPCHeursticDistance: java.lang.Iterable[Double] = {
//    val avatarPosition: Vector2d = so.getAvatarPosition
//    val npcPositions: Array[util.ArrayList[Observation]] = so.getNPCPositions(avatarPosition)
//    getHeuristicDistances(npcPositions)
//  }
//
//  def getPortalRealDistance: java.lang.Iterable[Double] = {
//    val avatarPosition: Vector2d = so.getAvatarPosition
//    val portalsPositions: Array[util.ArrayList[Observation]] = so.getPortalsPositions
//    getAStarDistances(portalsPositions)
//  }
//
//  @GPIgnore protected def flatObservations(observationsList:Array[util.ArrayList[Observation]]): util.List[Observation] = {
//    val result: util.List[Observation] = new util.ArrayList[Observation]
//    if (observationsList != null) {
//      for (observations <- observationsList) {
//        import scala.collection.JavaConversions._
//        for (observation <- observations) {
//          result.add(observation)
//        }
//      }
//    }
//    return result
//  }
//
//  @GPIgnore protected def getAStarDistances(observationsList: Array[util.ArrayList[Observation]]): util.List[Double] = {
//    val result: util.List[Double] = new util.ArrayList[Double]
//    val flatObs: util.List[Observation] = flatObservations(observationsList)
//
//    for (observation <- flatObs) {
//      val distance: Double = getAStarLength(so.getAvatarPosition, observation)
//      result.add(distance)
//    }
//    return result
//  }
//
//  @GPIgnore protected def getHeuristicDistances(observationsList: Array[util.ArrayList[Observation]]): util.List[Double] = {
//    val result: util.List[Double] = new util.ArrayList[Double]
//    val blockSize: Int = so.getBlockSize
//    if (observationsList != null) {
//      for (observations <- observationsList) {
//        import scala.collection.JavaConversions._
//        for (observation <- observations) {
//          val distance: Double = observation.sqDist / blockSize + countBlockingWalls(observation)
//          result.add(distance)
//        }
//      }
//    }
//    result
//  }
//
//  @GPIgnore protected def getAStarLength(avatarPosition: Vector2d, observation: Observation): Int = {
//    val blockSize: Int = so.getBlockSize
//    val start: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, so)
//    val goal: Position = new Position(observation.position.x.toInt / blockSize, observation.position.y.toInt / blockSize, so)
//    try {
//      return aStar.aStarLength(new AStarPathRequest[Position](start, goal))
//    }
//    catch {
//      case e: AStarException => {
//        return Integer.MAX_VALUE
//      }
//    }
//  }
//
//  @GPIgnore protected def countBlockingWalls(dstObservation: Observation): Double = {
//    var walls: Int = 0
//    val observationGrid: Array[Array[util.ArrayList[Observation]]] = so.getObservationGrid
//    var currentX: Int = so.getAvatarPosition.x.toInt / so.getBlockSize
//    var currentY: Int = so.getAvatarPosition.y.toInt / so.getBlockSize
//    if (currentX < 0 || currentY < 0) {
//      return 0
//    }
//    val dstX: Int = (dstObservation.position.x / so.getBlockSize).toInt
//    val dstY: Int = (dstObservation.position.y / so.getBlockSize).toInt
//    while (currentX != dstX || currentY != dstY) {
//      if (!observationGrid(currentX)(currentY).isEmpty) {
//        walls += 1
//      }
//      if (dstX > currentX) {
//        currentX += 1
//      }
//      else if (dstX < currentX) {
//        currentX -= 1
//      }
//      if (dstY > currentY) {
//        currentY += 1
//      }
//      else if (dstY < currentY) {
//        currentY -= 1
//      }
//    }
//     walls
//  }
//}