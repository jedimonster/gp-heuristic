package evolution_impl

import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Random

import controllers.Heuristics.StateHeuristic
import core.ArcadeMachine
import core.game.StateObservation
import evolution_engine.fitness.FitnessResult
import evolution_engine.selection.TournamentSelection
import evolution_engine.{EvolutionLogger, CSVEvolutionLogger, EvolutionRun}
import evolution_engine.evolution.{EvolutionParameters, ParentSelectionEvolutionStrategy}
import evolution_impl.crossover.{InTreeCrossoverAdapter, JavaCodeCrossover}
import evolution_impl.fitness.{IndividualHolder, MultiGameFitnessCalculator, SingleGameFitnessCalculator}
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.{WildRandomGrowInitializer, JavaCodeIndividual, RandomGrowInitializer, HeuristicIndividual}
import evolution_impl.gpprograms.trees.{HeuristicTreeIndividual, HeuristicTreeInitializer}
import evolution_impl.mutators.trees.{NodeThresholdPercentMutator, NodeThresholdMutator, RegrowHeuristicMutator, InTreeMutatorAdapter}
import evolution_impl.mutators._
import evolution_impl.search.{Position, AStar}

import scala.collection.immutable.IndexedSeq

/**
  * Created by itayaza on 24/11/2014.
  */

object GPRunHolder {
  var gpRun: ThreadedGPRun = null
}

class GPHeuristic() extends StateHeuristic {
  val gpRun: ThreadedGPRun = GPRunHolder.gpRun
  var individual: Option[HeuristicIndividual] = None

  // if we weren't given an individual, we have to hope the GP run will set one eventually.
  def waitForFirstIndividual() = {
    fitness.IndividualHolder.synchronized {
      while (individual.isEmpty && fitness.IndividualHolder.readyIndividual.isEmpty)
        fitness.IndividualHolder.wait()
    }
  }

  def useBestKnownIndividual() = {
    if (gpRun.isBestIndividualReady) {
      // at least one generation ended, we can use a proper individual.
      //      individual = gpRun.getBestIndividual
      individual = IndividualHolder.bestIndividual
    } else {
      // we have to apply some strategy for selecting the best ind from gen0, right now - random
      individual = IndividualHolder.readyIndividual
    }
  }

  override def evaluateState(stateObs: StateObservation): Double = {

    //    bestIndividual = gpRun.getBestIndividual
    val wrappedObservation = new StateObservationWrapper(stateObs, IndividualHolder.aStar)
    individual match {
      case Some(heuristic) => heuristic.run(wrappedObservation)
      case None => throw new RuntimeException("no individual to use for heuristic eval")
    }
  }
}


class ThreadedGPRun(logDirectory: String) extends Runnable {
  val treeCrossovers = new InTreeCrossoverAdapter(new JavaCodeCrossover(1.0), 0.3)

  val crossovers = new JavaCodeCrossover(0.25)
  val mutators = List(new ConstantsMutator(0.1),
    new ForLoopsMutator(0.15),
    new RegrowMethodMutator(0.05),
    new DropMethodMutator(0.05),
    new GrowNewwMethodMutator(0.05)
    //    ,    new AlphasMutator(0.2)
  )
  //  val treeMutators = List(new InTreeMutatorAdapter(0.3, mutators), new RegrowHeuristicMutator(0.2), new NodeThresholdMutator(0.1), new NodeThresholdPercentMutator(0.2))
  // todo add note threshold % change.

  val generations = 2000
  val popSize = 32
  val paramTypes = List(new StateObservationWrapper(null))

  val methodCount = 3
  val treeFitnessCalculator = new SingleGameFitnessCalculator[HeuristicTreeIndividual](ThreadedGPRun.gameName, independent = false, evaluationTimeout = 200)
  val fitnessCalculator = new SingleGameFitnessCalculator[JavaCodeIndividual](ThreadedGPRun.gameName, false, 100)
  //    with AlternatingPlayoutCalculator
  //  val fitnessCalculator = new MultiGameFitnessCalculator(cutoff = 2000)
  val selection = new TournamentSelection[JavaCodeIndividual](false)
  val treeSelection = new TournamentSelection[HeuristicTreeIndividual](false)

  //  val params = new EvolutionParameters[HeuristicTreeIndividual](treeFitnessCalculator, treeSelection,
  //    treeCrossovers, treeMutators, new HeuristicTreeInitializer(paramTypes, methodCount, 3), generations, popSize)
  val params = new EvolutionParameters[JavaCodeIndividual](fitnessCalculator, selection,
    crossovers, mutators, new WildRandomGrowInitializer(paramTypes, methodCount), generations, popSize)
  //    crossovers, mutators, new RandomGrowInitializer(paramTypes, methodCount), generations, popSize)

  //  var runningEvolution: EvolutionRun[HeuristicTreeIndividual] = null
  var runningEvolution: EvolutionRun[JavaCodeIndividual] = null

  def run() = {
    //    val logger = CSVEvolutionLogger.createCSVEvolutionLogger[JavaCodeIndividual](getNextLogDirectory(logDirectory))
    val logBaseDir = new File(logDirectory);
    if (!logBaseDir.exists())
      throw new RuntimeException("log directory doesn't exist")

    val logger = CSVEvolutionLogger.createCSVEvolutionLogger[JavaCodeIndividual](logBaseDir.toPath)

    params.setLogger(logger)

    runningEvolution = new EvolutionRun()
    runningEvolution.run(params, new ParentSelectionEvolutionStrategy[JavaCodeIndividual](params))
  }

  def stop() = {
    runningEvolution.stop()
  }

  def isBestIndividualReady: Boolean = params.bestIndividual.isDefined

  def getBestIndividual: Option[HeuristicIndividual] = {
    IndividualHolder.synchronized {
      while (IndividualHolder.bestIndividual == null) {
        IndividualHolder.bestIndividual.wait()
      }
      IndividualHolder.bestIndividual
    }
  }

  def getNextLogDirectory(logDirectory: String): Path = {
    var i = 1

    while (new File(logDirectory + Integer.toString(i)).exists())
      i += 1


    val file: File = new File(logDirectory + Integer.toString(i))
    file.mkdirs()
    file.toPath
  }
}

object ThreadedGPRun {
  // works on the fly: aliens, butterflies, missilecommand (sort of), frogs (almost)
  // works with unlimited time: aliens, boulderdash, butterflies, missilecommand, frogs, survivezombies, zelda
  // fails: chase
  // fails compilation/exception:
  // neg infinity?? portals,sokoban

  // cross validation set:
  // works with unlimited time: camelRace, firestorms, infection
  // pass but sucks with unlimited time: digdug, firecaster (but they all fail)
  // fail with unlimited time: overload
  val gameName = "plaqueattack"

  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  //  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl" + levelId + ".txt"


  def newInstance(logDirectory: String): ThreadedGPRun = {
    val run = new ThreadedGPRun(logDirectory)
    val thread = new Thread(run)
    thread.start()
    run
  }

  //      val gamesToPlay = List("aliens", "boulderdash", "butterflies", "chase", "frogs", "missilecommand", "portals", "sokoban", "survivezombies", "zelda"
  //        , "camelRace", "digdug", "firestorms", "infection", "firecaster", "overload", "pacman", "seaquest", "whackamole", "eggomania")
  //  val gamesToPlay = List("seaquest", "whackamole", "eggomania")
  val gamesToPlay = List(gameName)

  def main(args: Array[String]): Unit = {
    // create a new threaded GP run, it will update the best individual each gen.
    //    Thread.sleep(1000)

    if (args.length < 4) {
      System.err.println("Usage: run <visuals> <game>")
      System.exit(-1)
    }

    // run a game using the best individual known at each step
    val gamesResults = for (game <- gamesToPlay) yield {
      val logDirectory: String = args(3) + "/"
      val gameToPlay: String = args(1)
      val visuals: Boolean = args(0).equals("1")
      val repeat: Int = args(2).toInt

      GPRunHolder.gpRun = ThreadedGPRun.newInstance(logDirectory)
      val res = runNewGame(gameToPlay, visuals, repeat)
      GPRunHolder.gpRun.stop()
      res
    }
    println("finished playing, stopping evolution...")

    printf("Results: %s\n", gamesResults.toString)
  }


  def runNewGame(gameToPlay: String, visuals: Boolean, times: Int): GameRunResult = {
    val gpHeuristic: String = "controllers.heauristicGP.Agent"
    val gamePath = gamesPath + gameToPlay + ".txt"

    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    //Game and level to play
    println("---\nPlaying a game with evolving heuristic")
    val scores = for (i <- 0 to 4) yield {
      printf("[%s] Playing %s level %d, %d times\n", LocalDateTime.now().toString, gameToPlay, i, times - 1)

      val levelScores = for (j <- 0 to (times - 1)) yield {
        printf("[%s] Iteration %d", LocalDateTime.now().toString, j)
        Thread.sleep(1000)
        val levelPath = gamesPath + gameToPlay + "_lvl" + i + ".txt"
        IndividualHolder.resetAStar()
        ArcadeMachine.runOneGame(gamePath, levelPath, visuals, gpHeuristic, recordActionsFile, seed)
      }
      levelScores
    }

    printf("Scores for %s: %s\n", gameToPlay, scores.toString)
    //    printf("Average for %s: %s\n", gameToPlay, scores.sum / scores.size)

    new GameRunResult(gameToPlay, scores.head)
  }
}
