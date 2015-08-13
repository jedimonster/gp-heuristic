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
class SingleGameFitnessCalculator[I <: HeuristicIndividual]
(gameName: String,
 independent: Boolean = false,
 evaluationTimeout: Long = Int.MaxValue)
  extends FitnessCalculator[I]
  with PlayoutCalculator {

  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 1
  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl"
  //  + levelId + ".txt"
  var depthsReached = ListBuffer[Double]()
  val vGDLFactory = VGDLFactory.GetInstance().init()
  val vGDLRegistry = VGDLRegistry.GetInstance().init()

//  val intitialStates = {
//    this.synchronized {
//      val game: Game = new VGDLParser().parseGame(gamePath)
//      for (i <- 0 to 4) yield {
//        game.buildLevel(levelPath + i.toString + ".txt")
//        game.getObservation.copy()
//      }
//
//
//    }
//  }


  def getIndividualFitness(individual: I): Double = {
    individual.compile()
    IndividualHolder.synchronized {
      IndividualHolder.readyIndividual = Some(individual)
      IndividualHolder.notifyAll()
    }
    val n = 1
    var state: StateObservation = null
    IndividualHolder.synchronized {
      while (IndividualHolder.currentState == null) {
        IndividualHolder.wait()
      }
      state = IndividualHolder.currentState.copy()
    }
    try {
      // this can fail due to concurrency issues, since it means the old score is no longer relevent, and it's rare, we just try again.
      val scores = for (i <- 0 to n) yield {
        simulateGame(individual, state, cutoff = Int.MinValue)
      }

      scores.sum / (n + 1)
      //      scores.min
    } catch {
      // todo case compilation error drop it
      case e: ClassFormatError =>
        e.printStackTrace()
        sys.exit(-1)
      case e: Exception =>
        if (skipGen) {
          // the result is going to be ignored anyway.
          return 0.0
        }
        println("Failed fitness evaluation - retrying")
        getIndividualFitness(individual)
    }
  }

  def simulateGame(individual: I, stateObservation: StateObservation, cutoff: Int): Double = {
    var state: StateObservation = stateObservation.copy()


    val playoutResult: (Double, Double, Int) = adjustableWidthPlayout(individual, state, 2, 5) // score, heuristic score, depth
    val score = playoutResult._1

    depthsReached.append(playoutResult._3)

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

  override def processResult(result: FitnessResult[I]): Unit = {
    val fitnessValues = result.getMap
    val best = fitnessValues.maxBy(x => x._2)
    val averageDepth: Double = depthsReached.sum / depthsReached.size

    printf("Gen %d\nBest fitness - %s - %s\n", gen, best._2, best._1.getName)
    printf("average depth reached %f\n", averageDepth)
    printf("min depth reached %f\n", depthsReached.min)

    //    printf("%f", getIndividualFitness((new MinDistanceToImmovableHeuristic).asInstanceOf[I]))

    depthsReached = ListBuffer[Double]()

    // update the best individual for the evolving heuristic agent.

    IndividualHolder.synchronized {
      IndividualHolder.bestIndividual = Some(best._1)
      IndividualHolder.notifyAll()
    }

    gen += 1


  }
}

object IndividualHolder {
  var readyIndividual: Option[HeuristicIndividual] = None
  // filled by the first gen so we can start
  var currentIndividual: Option[HeuristicIndividual] = None
  // can be used to evaluate fitness of a given heuristic
  var bestIndividual: Option[HeuristicIndividual] = None
  // best known individual per generation
  var currentState: StateObservation = null
  // updated by the real time agent each turn
  var aStar = new AStar[Position]()

  def resetAStar(): Unit = {
    aStar = new AStar[Position]();
  }
}


