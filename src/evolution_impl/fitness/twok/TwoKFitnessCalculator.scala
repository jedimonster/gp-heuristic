package evolution_impl.fitness.twok

import evolution_engine.fitness.{FitnessResult, FitnessCalculator}
import evolution_impl.gpprograms.base.HeuristicIndividual

/**
  * Created by Itay on 19-Jan-16.
  */
class TwoKFitnessCalculator
[I <: HeuristicIndividual]
(gameName: String,
 independent: Boolean = false,
 evaluationTimeout: Long = Int.MaxValue)
  extends FitnessCalculator[I] {
  override def getIndividualFitness(individual: I): Double = {

    ???
  }

  override def processResult(result: FitnessResult[I]): Unit = ???
}
