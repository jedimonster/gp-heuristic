package evolution_impl.fitness.twok

import java.time.Duration
import java.util.function.Supplier

import evolution_engine.fitness.{FitnessResult, FitnessCalculator}
import evolution_impl.gpprograms.base.HeuristicIndividual
import org.apache.commons.math3.random.{RandomDataGenerator, MersenneTwister}
import org.apache.commons.math3.stat.descriptive.{SummaryStatistics, StatisticalSummaryValues}
import put.game2048.{Agent, Game}

import scala.util.Random

/**
  * Created by Itay on 19-Jan-16.
  */
class TwoKFitnessCalculator
[I <: HeuristicIndividual]
()
  extends FitnessCalculator[I] {
  override def getIndividualFitness(individual: I): Double = {
    individual.compile()
    val agent = new HeuristicAgent(individual)
    val random = new RandomDataGenerator(new MersenneTwister(Random.nextInt()))
    val game: Game = new Game(Duration.ofMillis(1L))
    val result = game.playMultiple(new Supplier[Agent] {
      override def get(): Agent = agent
    }, 1000, random)
    result.getScore.getMean
  }

  override def processResult(result: FitnessResult[I]): Unit = {
    val statistics: SummaryStatistics = new SummaryStatistics()
    val fitnessValues: Iterable[Double] = result.getMap.values
    for (value <- fitnessValues)
      statistics.addValue(value)

    println(statistics.getMean)
    println(statistics.getMax)
  }
}
