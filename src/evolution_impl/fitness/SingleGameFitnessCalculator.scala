package evolution_impl.fitness

import java.util.Random

import core.game.{Game, StateObservation}
import core.{ArcadeMachine, VGDLFactory, VGDLParser, VGDLRegistry}
import evolution_engine.fitness.{FitnessCalculator, FitnessResult}
import evolution_impl.gpprograms.base.{HeuristicIndividual, JavaCodeIndividual}
import evolution_impl.gpprograms.trees.HeuristicTreeIndividual
import evolution_impl.search.{AStar, Position}
import tools.ElapsedCpuTimer

import scala.collection.mutable.ListBuffer


/**
 * Created by itayaza on 24/11/2014.
 */
class SingleGameFitnessCalculator[I <: HeuristicIndividual](gameName: String) extends FitnessCalculator[I] with PlayoutCalculator {
  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl" + levelId + ".txt"
  var depthsReached = ListBuffer[Double]()
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

  override def processResult(result: FitnessResult[I]): Unit = {
    val fitnessValues = result.getMap
    val best = fitnessValues.maxBy(x => x._2)
    val averageDepth: Double = depthsReached.sum / depthsReached.size

    printf("Gen %d\nBest fitness - %s - %s\n", gen, best._2, best._1.getName)
    printf("average depth reached %f\n", averageDepth)

    // update the best individual for the evolving heuristic agent.

    IndividualHolder.synchronized {
      IndividualHolder.bestIndividual = Some(best._1)
      IndividualHolder.notifyAll()
    }
    //    val realScore = playGame(best._1)
    //      println("real score with it " + realScore)
    //      println("playout score with it " + getIndividualFitness(best._1))
    //      if (best._2 > 76) {
    //      val in = readLine("Show best individuals game(Y/N)?")
    gen += 1

    //      if (gen % 5 == 0)
    //        playGame(best._1, visuals = true)

    //        }
  }

  def getIndividualFitness(individual: I): Double = {
    IndividualHolder.synchronized {
      IndividualHolder.currentIndividual = Some(individual)
      IndividualHolder.notifyAll()
    }
    // individuals compile themselves.
    //    individual.compile()
    IndividualHolder.synchronized {
      IndividualHolder.readyIndividual = Some(individual)
    }
    val n = 0
    try {
      // this can fail due to concurrency issues, since it means the old score is no longer relevent, and it's rare, we just try again.
      val scores = for (i <- 0 to n)
        yield simulateGame(individual, cutoff = Int.MinValue)

      scores.sum / (n + 1)
    } catch {
      // todo case compilation error drop it
      case e: ClassFormatError =>
        e.printStackTrace()
        sys.exit(-1)
      case e: Exception =>
        println("FAILED FITNESS EVALUATION - RETRYING")
        getIndividualFitness(individual)
    }
  }


  def simulateGame(individual: I, cutoff: Int): Double = {
    var state: StateObservation = null
    //    val playoutState = playout(individual, state, cutoff)
    //    playoutState.getGameScore

    //    state = intitialState.copy
    IndividualHolder.synchronized {
      while (IndividualHolder.currentState == null) {
        IndividualHolder.wait()
      }
      state = IndividualHolder.currentState
    }
    val timer = new ElapsedCpuTimer()
    //        timer.setMaxTimeMillis(Int.MaxValue)
    timer.setMaxTimeMillis(100)
    val playoutResult: (Double, Double, Int) = rec_playout(individual, state, timer) // score, heuristic score, depth
    val score = playoutResult._1
    depthsReached.append(playoutResult._3)
    //        printf("played out in %dms \n", timer.elapsedMillis())

    score
  }

  def playGame(individual: I, visuals: Boolean = false) = {
    val gpHeuristic: String = "evolution_impl.fitness.dummyagent.Agent"

    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    //Game and level to play
    println("---\nPlaying a game with " + individual.getName)
    val scores = for (i <- 0.to(0)) yield {
      val levelPath = gamesPath + gameName + "_lvl" + i + ".txt"
      IndividualHolder.synchronized {
        IndividualHolder.currentIndividual = Some(individual)
        ArcadeMachine.runOneGame(gamePath, levelPath, true, gpHeuristic, recordActionsFile, seed)
      }
    }
    //    val score2: Double = ArcadeMachine.runOneGame(game, level1, false, gpHeuristic, recordActionsFile, seed)
    //
    //    (score + score2) / 2
    scores.sum / scores.size
  }
}

object IndividualHolder {
  var readyIndividual: Option[HeuristicIndividual] = None
  var currentIndividual: Option[HeuristicIndividual] = None
  var bestIndividual: Option[HeuristicIndividual] = None
  var currentState: StateObservation = null
  var aStar = new AStar[Position]()
}


