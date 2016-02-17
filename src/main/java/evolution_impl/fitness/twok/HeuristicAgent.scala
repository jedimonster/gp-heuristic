package evolution_impl.fitness.twok

import java.time.Duration
import java.util

import evolution_impl.gpprograms.base.{HeuristicIndividual, JavaCodeIndividual}
import put.ci.cevo.games.game2048.State2048
import put.game2048.{Action, Board, Agent}
import scalaj.collection.Imports._

import scala.util.Random

/**
  * Created by Itay on 19-Jan-16.
  */
class HeuristicAgent(heuristic: HeuristicIndividual) extends Agent {
  override def chooseAction(board: Board, availableActions: util.List[Action], duration: Duration): Action = {
    val possibleScores = for (action <- availableActions.asScala) yield {
      val nextState = new BoardWrapper(board, action.ordinal()).copy
      val score: Double = heuristic.run(nextState)
      (action, score)
    }

    possibleScores.maxBy((f) => f._2)._1
  }
}
