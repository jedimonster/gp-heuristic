//package evolution_impl.fitness
//
//import core.game.StateObservation
//import evolution_impl.gpprograms.base.{WildJavaCodeIndividual, HeuristicIndividual}
//import ontology.Types.ACTIONS
//
///**
// * Created by Itay on 8/9/2015.
// */
//trait AlternatingPlayoutCalculator extends PlayoutCalculator {
//  val N = 70
//
//  override def playout(individual: HeuristicIndividual, stateObservation: StateObservation): (Double, Double, Int) = {
//    var playoutResult = new ActionResult(ACTIONS.ACTION_NIL, stateObservation.getGameScore, 0.0, 0, stateObservation)
//
//    individual match {
//      case i: WildJavaCodeIndividual =>
//        for (a <- i.alphas) {
//          val oneSteps = (a * N).toInt
//          val wides = (Math.pow(1 - a, 3) * N).toInt
//
//          for (i <- 0 to wides) {
//            val prevState = playoutResult.stateObservation
//            playoutResult = maxStateToDepth(individual, ACTIONS.ACTION_NIL, prevState)
//          }
//
//          playoutResult = recPlayout(individual, stateObservation, ACTIONS.ACTION_NIL, oneSteps)
//        }
//        (playoutResult.gameScore, playoutResult.heuristicScore, playoutResult.depth)
//      case _ =>
//        throw new RuntimeException("wrong usage of AlternatingPlayoutCalculator")
//    }
//
//  }
//}
