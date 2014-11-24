package evolution_impl

import java.util.Random

import core.ArcadeMachine
import evolution_engine.fitness.{FitnessResult, FitnessCalculator}
import evolution_impl.gpprograms.JavaCodeIndividual

/**
 * Created by itayaza on 24/11/2014.
 */
class SingleGameFitnessCalculator(game: String) extends FitnessCalculator[JavaCodeIndividual] {
  override def calculateFitness(individuals: List[JavaCodeIndividual]): FitnessResult[JavaCodeIndividual] = {
    val fitnessValues = for (i <- individuals) yield (i, getIndividualFitness(i))
    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  }

  def getIndividualFitness(individual: JavaCodeIndividual) = {
    CurrentIndividualHolder.individual = individual
    individual.compile()
    // run the machine using the dummy agent here
    val gpHeuristic: String = "evolution_impl.fitness.dummyagent.Agent"

    //Available games:
    val gamesPath: String = "gvgai/examples/gridphysics/"

    //CIG 2014 Training Set Games
    //String games[] = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
    //        "missilecommand", "portals", "sokoban", "survivezombies", "zelda"};
    //CIG 2014 Validation Set Games
    val games = Array[String]("camelRace", "digdug", "firestorms", "infection", "firecaster", "overload", "pacman", "seaquest", "whackamole", "eggomania")


    //Other settings
    val visuals: Boolean = false
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    //Game and level to play
    val gameIdx: Int = 0
    val levelIdx: Int = 0
    val game: String = gamesPath + games(gameIdx) + ".txt"

    val level1: String = gamesPath + games(gameIdx) + "_lvl" + levelIdx + ".txt"

    val score: Double = ArcadeMachine.runOneGame(game, level1, visuals, gpHeuristic, recordActionsFile, seed)
    score
  }
}

object CurrentIndividualHolder {
  var individual: JavaCodeIndividual = null
}


