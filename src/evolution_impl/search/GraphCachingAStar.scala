package evolution_impl.search

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created By Itay Azaria
 * Date: 21/07/2015
 */
class GraphCachingAStar[N <: GraphNode[N]](root: N) extends AStar[N] {
  var nodes: mutable.HashMap[N, N] with mutable.SynchronizedMap[N, N] = expandNode(root)

  def expandNode(root: N): mutable.HashMap[N, N] with mutable.SynchronizedMap[N, N] = {
    val closedSet = new mutable.HashMap[N, N]() with mutable.SynchronizedMap[N, N]
    val openSet: ListBuffer[N] = ListBuffer[N]()
    //    closedSet.put(root, root)
    openSet append root

    while (openSet.nonEmpty) {
      val current = openSet remove 0
      if (!closedSet.contains(current)) {
        closedSet put(current, current)
        for (neighbor <- current.getNeighbors) {
          if (!(closedSet contains neighbor)) {
            openSet append neighbor
          }
        }
      }

    }
    closedSet
  }

  override def aStar(pathRequest: AStarPathRequest[N]): List[N] = {
    if (!(nodes contains pathRequest.start)) {
//      nodes.clear()
      nodes ++= expandNode(pathRequest.start)
    }
    if (!(nodes contains pathRequest.end)) {
      nodes ++= (expandNode(pathRequest.end))
    }
    if (!(nodes contains pathRequest.start) || !(nodes contains pathRequest.end))
      return super.aStar(pathRequest)
    val startNode: N = nodes(pathRequest.start)
    val goalNode: N = nodes(pathRequest.end)
    val newRequest = new AStarPathRequest[N](startNode, goalNode)
    super.aStar(newRequest)
  }
}
