package evolution_impl.fitness

import java.util.Random

import core.ArcadeMachine
import evolution_engine.fitness.{FitnessCalculator, FitnessResult}
import evolution_impl.DumbFitnessResult
import evolution_impl.gpprograms.JavaCodeIndividual

/**
 * Created by itayaza on 24/11/2014.
 */
class SingleGameFitnessCalculator(game: String) extends FitnessCalculator[JavaCodeIndividual] {
  override def calculateFitness(individuals: List[JavaCodeIndividual]): FitnessResult[JavaCodeIndividual] = {
    val fitnessValues: List[(JavaCodeIndividual, Double)] = for (i <- individuals) yield (i, getIndividualFitness(i))
    val best = fitnessValues.maxBy(x => x._2)
    CurrentIndividualHolder.indLock.synchronized {
      CurrentIndividualHolder.individual = best._1
      println("Best fitness - " + best._2)
//      if (best._2 > 76) {
//        val in = readLine("Show best individuals game(Y/N)?")
//        if (in.equalsIgnoreCase("y"))
//          playGame(best._1, visuals = true)
//      }
    }
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }

  def getIndividualFitness(individual: JavaCodeIndividual) = {
    CurrentIndividualHolder.indLock.synchronized {
      CurrentIndividualHolder.individual = individual
      individual.compile()
      // run the machine using the dummy agent here
      playGame(individual)
    }
  }

  def playGame(individual: JavaCodeIndividual, visuals: Boolean = false) = {
    val gpHeuristic: String = "evolution_impl.fitness.dummyagent.Agent"

    // todo take game/lvl from fields.

    //Available games:1
    val gamesPath: String = "gvgai/examples/gridphysics/"

    //CIG 2014 Training Set Games
    val games = Array[String]("aliens", "boulderdash", "butterflies", "chase", "frogs",
      "missilecommand", "portals", "sokoban", "survivezombies", "zelda")
    //CIG 2014 Validation Set Games
    //    val games = Array[String]("camelRace", "digdug", "firestorms", "infection", "firecaster", "overload", "pacman", "seaquest", "whackamole", "eggomania")


    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    //Game and level to play
    val gameIdx: Int = 0
    val levelIdx: Int = 0
    val game: String = gamesPath + games(gameIdx) + ".txt"

    val level1: String = gamesPath + games(gameIdx) + "_lvl" + levelIdx + ".txt"

    println("---\nPlaying a game with " + individual.getName)

    val score: Double = ArcadeMachine.runOneGame(game, level1, visuals, gpHeuristic, recordActionsFile, seed)
    //    val score2: Double = ArcadeMachine.runOneGame(game, level1, false, gpHeuristic, recordActionsFile, seed)
    //
    //    (score + score2) / 2
    score
  }
}

object CurrentIndividualHolder {
  val indLock = None
  var individual: JavaCodeIndividual = null
}


