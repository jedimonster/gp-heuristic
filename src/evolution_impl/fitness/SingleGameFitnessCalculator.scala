package evolution_impl.fitness

import java.util.Random

import core.game.Game
import core.{VGDLParser, VGDLRegistry, VGDLFactory, ArcadeMachine}
import evolution_engine.fitness.{FitnessCalculator, FitnessResult}
import evolution_impl.DumbFitnessResult
import evolution_impl.gpprograms.JavaCodeIndividual
import tools.ElapsedCpuTimer

/**
 * Created by itayaza on 24/11/2014.
 */
class SingleGameFitnessCalculator(gameName: String) extends FitnessCalculator[JavaCodeIndividual] with PlayoutCalculator {
  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl" + levelId + ".txt"

  override def calculateFitness(individuals: List[JavaCodeIndividual]): FitnessResult[JavaCodeIndividual] = {
    val fitnessValues: List[(JavaCodeIndividual, Double)] = for (i <- individuals) yield (i, getIndividualFitness(i))
    val best = fitnessValues.maxBy(x => x._2)
    CurrentIndividualHolder.indLock.synchronized {
      CurrentIndividualHolder.individual = best._1
      println("Best fitness - " + best._2)
      val realScore = playGame(best._1)
      println("real score with it " + realScore)
      println("playout score with it " + getIndividualFitness(best._1))
      //      if (best._2 > 76) {
      //        val in = readLine("Show best individuals game(Y/N)?")
      //        if (in.equalsIgnoreCase("y"))
      //          playGame(best._1, visuals = true)
      //      }
    }
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }

  def getIndividualFitness(individual: JavaCodeIndividual) = {
    //    CurrentIndividualHolder.indLock.synchronized {
    //      CurrentIndividualHolder.individual = individual
    individual.compile()
    // run the machine using the dummy agent here
    val s1 = simulateGame(individual, cutoff = Int.MaxValue)
    val s2 = simulateGame(individual, cutoff = Int.MaxValue)
    (s1+s2)/2
    //    }
  }

  def simulateGame(individual: JavaCodeIndividual, cutoff: Int): Double = {
    VGDLFactory.GetInstance.init
    VGDLRegistry.GetInstance.init
    val toPlay: Game = new VGDLParser().parseGame(gamePath)
    toPlay.buildLevel(levelPath)
    val state = toPlay.getObservation

    //    val playoutState = playout(individual, state, cutoff)
    //    playoutState.getGameScore
    val timer = new ElapsedCpuTimer()
    timer.setMaxTimeMillis(Int.MaxValue)
    val score = rec_playout(individual, state, timer)._1
    //    printf("played out in %dms \n", timer.elapsedMillis())
    score
  }

  def playGame(individual: JavaCodeIndividual, visuals: Boolean = false) = {
    val gpHeuristic: String = "evolution_impl.fitness.dummyagent.Agent"

    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    //Game and level to play
    println("---\nPlaying a game with " + individual.getName)
    val scores = for (i <- 0.to(4)) yield {
      val levelPath = gamesPath + gameName + "_lvl" + i + ".txt"
      ArcadeMachine.runOneGame(gamePath, levelPath, visuals, gpHeuristic, recordActionsFile, seed)
    }
    //    val score2: Double = ArcadeMachine.runOneGame(game, level1, false, gpHeuristic, recordActionsFile, seed)
    //
    //    (score + score2) / 2
    scores.sum / scores.size
  }
}

object CurrentIndividualHolder {
  val indLock = None
  var individual: JavaCodeIndividual = null
}


