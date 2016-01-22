package evolution_impl.fitness.twok

import java.util

import evolution_impl.fitness.dummyagent.GPIgnore
import put.ci.cevo.games.game2048.State2048
import put.game2048.Board
import scalaj.collection.Imports._

/**
  * Created by Itay on 20-Jan-16.
  */
class BoardWrapper(board: Board) {
  @GPIgnore
  def copy = {
    val newArray: Array[Array[Int]] = board.get.clone
    new BoardWrapper(new Board(newArray))
  }

  def getState = new State2048(board.get)

  def getFeatures: java.lang.Iterable[java.lang.Double] = {
    val features: util.ArrayList[java.lang.Double] = new util.ArrayList[java.lang.Double]()
    for (f <- getState.getFeatures) {
      features.add(new java.lang.Double(f))
    }
    features
  }

}
