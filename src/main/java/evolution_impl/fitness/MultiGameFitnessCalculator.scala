package evolution_impl.fitness

import core.{VGDLParser, VGDLRegistry, VGDLFactory}
import core.game.{Game, StateObservation}
import evolution_engine.fitness.{FitnessResult, FitnessCalculator}
import evolution_impl.DumbFitnessResult
import evolution_impl.gpprograms.base.JavaCodeIndividual

import scala.collection.mutable.ListBuffer
import scalaj.collection.Imports._

/**
 * Created by itayaza on 03/02/2015.
 */
class MultiGameFitnessCalculator(cutoff: Int = Int.MaxValue) extends FitnessCalculator[JavaCodeIndividual] with PlayoutCalculator {

  //  def calculateFitness(individuals: List[JavaCodeIndividual]): FitnessResult[JavaCodeIndividual] = {
  //    val fitnessValues: List[(JavaCodeIndividual, Double)] = for (i <- individuals) yield (i, getIndividualFitness(i))
  //    val best = fitnessValues.maxBy(x => x._2)
  //    fullEvaluate(best._1)
  //
  //    new DumbFitnessResult[JavaCodeIndividual](fitnessValues.toMap)
  //  }
  override def processResult(result: FitnessResult[JavaCodeIndividual]): Unit = {}

  def fullEvaluate(individual: JavaCodeIndividual) = {
    //CIG 2014 Training Set Games
    var games = Array[String]("aliens", "boulderdash", "butterflies", "chase", "frogs", "missilecommand", "portals", "sokoban", "survivezombies", "zelda")
    val gamesPath: String = "gvgai/examples/gridphysics/"

    val levelId = 0
    val scores: ListBuffer[Double] = ListBuffer()


    for (gameId <- 0 to 9) {
      val gameStr: String = gamesPath + games(gameId) + ".txt"
      val levelStr: String = gamesPath + games(gameId) + "_lvl" + levelId + ".txt"

      val playoutScore: Double = playGame(individual, gameStr, levelStr, Int.MaxValue)
      //      println("score for game " + gameId + " " + playoutScore)

      scores.append(playoutScore)
    }
    println("Scores on training set = ", scores)

    games = Array[String]("camelRace", "digdug", "firestorms", "infection", "firecaster", "overload", "pacman", "seaquest", "whackamole", "eggomania")
    for (gameId <- 0 to 9) {
      val gameStr: String = gamesPath + games(gameId) + ".txt"
      val levelStr: String = gamesPath + games(gameId) + "_lvl" + levelId + ".txt"

      val playoutScore: Double = playGame(individual, gameStr, levelStr, Int.MaxValue)
      //      println("score for game " + gameId + " " + playoutScore)

      scores.append(playoutScore)
    }
    println("Scores on test set = ", scores)

  }


  def getIndividualFitness(individual: JavaCodeIndividual): Double = {
    println("Evaluating " + individual.getName)

    //CIG 2014 Training Set Games
    val games = Array[String]("aliens", "boulderdash", "butterflies", "chase", "frogs", "missilecommand", "portals", "sokoban", "survivezombies", "zelda")
    val gamesPath: String = "gvgai/examples/gridphysics/"

    val levelId = 0
    val scores: ListBuffer[Double] = ListBuffer()

    for (gameId <- 0 to 9) {
      val gameStr: String = gamesPath + games(gameId) + ".txt"
      val levelStr: String = gamesPath + games(gameId) + "_lvl" + levelId + ".txt"

      val playoutScore: Double = playGame(individual, gameStr, levelStr, cutoff)
      //      println("score for game " + gameId + " " + playoutScore)

      scores.append(playoutScore)
    }

    println("Scores: " + scores)

    val avrg = scores.foldLeft(0.0)(_ + _) / scores.length
    println("Avg = " + avrg)

    avrg
  }

  def playGame(individual: JavaCodeIndividual, gameStr: String, levelStr: String, cutoff: Int): Double = {
    VGDLFactory.GetInstance.init
    VGDLRegistry.GetInstance.init
    val toPlay: Game = new VGDLParser().parseGame(gameStr)
    toPlay.buildLevel(levelStr)
    val state = toPlay.getObservation

    playout(individual, state, cutoff)

  }
}
