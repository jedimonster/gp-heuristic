package evolution_impl

import scala.collection.immutable.IndexedSeq

/**
 * Created by Itay on 28/06/2015.
 */
class GameRunResult(val name: String, val scores: IndexedSeq[Double]) {
  override def toString = name + " " + scores.toString
}
