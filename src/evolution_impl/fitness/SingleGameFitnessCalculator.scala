package evolution_impl.fitness

import java.util.Random

import core.game.{StateObservation, Game}
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

  val vGDLFactory = VGDLFactory.GetInstance().init()
  val vGDLRegistry = VGDLRegistry.GetInstance().init()

  def intitialState = {
    this.synchronized {
      val game: Game = new VGDLParser().parseGame(gamePath)

      game.buildLevel(levelPath)


      game.getObservation.copy()
    }
  }

  var gen = 0

  override def processResult(result: FitnessResult[JavaCodeIndividual]): Unit = {
    val fitnessValues = result.getMap
    val best = fitnessValues.maxBy(x => x._2)
    printf("Best fitness - %s - %s\n", best._2, best._1.getName)

    // update the best individual for the evolving heuristic agent.

    IndividualHolder.synchronized {
      IndividualHolder.bestIndividual = best._1
      IndividualHolder.notifyAll()
    }
    //      val realScore = playGame(best._1)
    //      println("real score with it " + realScore)
    //      println("playout score with it " + getIndividualFitness(best._1))
    //      if (best._2 > 76) {
    //      val in = readLine("Show best individuals game(Y/N)?")
    gen += 1

    //      if (gen % 5 == 0)
    //        playGame(best._1, visuals = true)

    //        }
  }

  def getIndividualFitness(individual: JavaCodeIndividual): Double = {
    IndividualHolder.synchronized {
      IndividualHolder.currentIndividual = individual
      IndividualHolder.notifyAll()
    }
    individual.compile()

    val n = 0
    try {
      // this can fail due to concurrency issues, since it means the old score is no longer relevent, and it's rare, we just try again.
      val scores = for (i <- 0 to n)
        yield simulateGame(individual, cutoff = Int.MinValue)

      scores.sum / (n + 1)
    } catch {
      case e: Exception =>
        println("FAILED FITNESS EVALUATION - RETRYING")
        getIndividualFitness(individual)
    }
  }

  def simulateGame(individual: JavaCodeIndividual, cutoff: Int): Double = {
    var state: StateObservation = null
    //    val playoutState = playout(individual, state, cutoff)
    //    playoutState.getGameScore

    //    val state = intitialState.copy
    IndividualHolder.synchronized {
      while (IndividualHolder.currentState == null) {
        IndividualHolder.wait()
      }
      state = IndividualHolder.currentState
    }
    val timer = new ElapsedCpuTimer()
    timer.setMaxTimeMillis(Int.MaxValue)
    //    timer.setMaxTimeMillis(250)
    val score = rec_playout(individual, state, timer)._1
    //    printf("played out in %dms \n", timer.elapsedMillis())

    score
  }

  def playGame(individual: JavaCodeIndividual, visuals: Boolean = false) = {
    //    val gpHeuristic: String = "evolution_impl.fitness.dummyagent.Agent"
    //
    //    //Other settings
    //    val recordActionsFile: String = null
    //    val seed: Int = new Random().nextInt
    //
    //    //Game and level to play
    //    println("---\nPlaying a game with " + individual.getName)
    //    val scores = for (i <- 0.to(4)) yield {
    //      val levelPath = gamesPath + gameName + "_lvl" + i + ".txt"
    //      ArcadeMachine.runOneGame(gamePath, levelPath, visuals, gpHeuristic, recordActionsFile, seed)
    //    }
    //    //    val score2: Double = ArcadeMachine.runOneGame(game, level1, false, gpHeuristic, recordActionsFile, seed)
    //    //
    //    //    (score + score2) / 2
    //    scores.sum / scores.size
  }
}

object IndividualHolder {
  var currentIndividual: JavaCodeIndividual = null
  var bestIndividual: JavaCodeIndividual = null
  var currentState: StateObservation = null
}


