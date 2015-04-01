package evolution_impl.search

import java.util
import java.util.Arrays

import core.game.{StateObservation, Observation}
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import scalaj.collection.Imports._

import scala.collection.mutable.ListBuffer

/**
 * Created by Itay on 25/03/2015.
 */
class Position(val x: Int, val y: Int, so: StateObservation) extends GraphNode[Position] {

  override def getNeighbors: List[Position] = {
    val immovablePositions = new StateObservationWrapper(so).getImmovablePositions(0, 0).iterator().asScala.toSet
    val candidates: List[Position] = List(new Position(x - 1, y, so), new Position(x + 1, y, so), new Position(x, y - 1, so), new Position(x, y + 1, so))
    val observationGrid: Array[Array[java.util.ArrayList[Observation]]] = so.getObservationGrid
    val neighbors = ListBuffer[Position]()
    var wall: Boolean = false

    for (candidate: Position <- candidates) {
      wall = false
      if (!(candidate.x >= observationGrid.length || candidate.x < 0 || candidate.y >= observationGrid(0).length || candidate.y < 0)) {
        // if the candidate is in range

        val observations: util.ArrayList[Observation] = observationGrid(candidate.x)(candidate.y)
        var i: Int = 0
        while (i < observations.size && !wall) {
          val observation: Observation = observations.get(i)

          wall = immovablePositions.contains(observation) // todo for some reason resources might appear here..
          i += 1
        }

        if (!wall)
          neighbors.append(candidate)
      }
    }

    neighbors.toList
  }

  def heuristicDistance(goal: Position): Double = {
    Math.abs(x - goal.x) + Math.abs(y - goal.y)
//    0
  }

  override def toString: String = {
    "Position{" + "x=" + x + ", y=" + y + '}'
  }

  override def equals(other: Any): Boolean = {
    other match {
      case position: Position => x == position.x && y == position.y
      case _ => false
    }
  }

  override def hashCode: Int = {
    31 * x + y
  }
}