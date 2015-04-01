package evolution_impl.search

import java.util.Comparator

import evolution_impl.fitness.IndividualHolder

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created By Itay Azaria
 * Date: 4/1/2015
 */
class AStar[N <: GraphNode[N]] {
//  val aStarCache = new mutable.HashMap[AStarPathRequest[GraphNode], List[GraphNode]] with mutable.SynchronizedMap[AStarPathRequest[N], List[GraphNode[N]]]

  def aStarLength(pathRequest: AStarPathRequest[N]): Int = {
    val path: List[N] = aStar(pathRequest)
    path.size
  }

  def aStar(pathRequest: AStarPathRequest[N]): List[N] = {
//    if (IndividualHolder.aStarCache.contains(pathRequest)) {
//      println("Restoring path from cache") // todo we can fail between .contains and .get
//      return aStarCache.get(pathRequest).get
//    }
    val start = pathRequest.start
    val goal = pathRequest.end
    val closedSet: mutable.HashSet[N] = new mutable.HashSet[N]
    val fScore: mutable.HashMap[N, Double] = new mutable.HashMap[N, Double]()
    val openSet: SortedList[N] = new SortedList[N](new Comparator[N] {
      override def compare(o1: N, o2: N): Int = fScore.get(o1).get.compareTo(fScore.get(o2).get)
    })
    val cameFrom: mutable.HashMap[N, N] = new mutable.HashMap[N, N]
    val gScore: mutable.HashMap[N, Double] = new mutable.HashMap[N, Double]

    openSet.add(start)
    gScore.put(start, 0.0)
    fScore.put(start, gScore.get(start).get + start.heuristicDistance(goal))
    while (!openSet.isEmpty) {
      val current: N = openSet.get(0)
      if (current equals goal)
        return reconstructPath(cameFrom, goal)

      openSet.remove(current)
      closedSet.add(current)

      for (neighbor <- current.getNeighbors) {
        if (closedSet.contains(neighbor)) {}
        else {
          val tentativeGScore: Double = gScore.get(current).get + 1
          if (!openSet.contains(neighbor) || tentativeGScore < gScore.get(neighbor).get) {
            cameFrom.put(neighbor, current)
            gScore.put(neighbor, tentativeGScore)
            fScore.put(neighbor, gScore.get(neighbor).get + neighbor.heuristicDistance(goal))
            if (!openSet.contains(neighbor))
              openSet.add(neighbor)
          }
        }
      }
    }


    throw new AStarException("A* Failed")
  }

  private def reconstructPath(cameFrom: mutable.HashMap[N, N], goalNode: N): List[N] = {
    var currentNode: N = goalNode
    val path: ListBuffer[N] = ListBuffer()
    path.append(currentNode)

    while (cameFrom.contains(currentNode)) {
      currentNode = cameFrom.get(currentNode) match {
        case Some(n) => n
        case _ => throw new AStarException("Can't reconstruct path")
      }
      path.prepend(currentNode)
//      aStarCache.put(new AStarPathRequest[N](currentNode, goalNode), path.toList)
    }

    path.toList
  }
}

class AStarPathRequest[N <: GraphNode[N]](val start: N, val end: N)

abstract class GraphNode[N <: GraphNode[N]] {
  def getNeighbors: List[N]

  def heuristicDistance(toNode: N): Double

  override def equals(obj: scala.Any): Boolean
}
