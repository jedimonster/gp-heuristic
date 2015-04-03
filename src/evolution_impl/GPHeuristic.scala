package evolution_impl

import java.io.File
import java.nio.file.Path
import java.util.Random

import controllers.Heuristics.StateHeuristic
import core.ArcadeMachine
import core.game.StateObservation
import evolution_engine.selection.TournamentSelection
import evolution_engine.{CSVEvolutionLogger, Run}
import evolution_engine.evolution.{EvolutionParameters, ParentSelectionEvolutionStrategy}
import evolution_impl.crossover.JavaCodeCrossover
import evolution_impl.fitness.{IndividualHolder, MultiGameFitnessCalculator, SingleGameFitnessCalculator}
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.{RandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.mutators.{RegrowMethodMutator, ForLoopsMutator, ConstantsMutator}

/**
 * Created by itayaza on 24/11/2014.
 */

object GPRunHolder {
  var gpRun: ThreadedGPRun = null
}

class GPHeuristic() extends StateHeuristic {
  val gpRun: ThreadedGPRun = GPRunHolder.gpRun
  var individual: Option[JavaCodeIndividual] = None

  // if we weren't given an individual, we have to hope the GP run will set one eventually.
  def waitForFirstIndividual() = {
    fitness.IndividualHolder.synchronized {
      while (individual == null && fitness.IndividualHolder.currentIndividual == null)
        fitness.IndividualHolder.wait()
    }
  }

  def useBestKnownIndividual() = {
    if (gpRun.isBestIndividualReady) {
      // at least one generation ended, we can use a proper individual.
      individual = gpRun.getBestIndividual
    } else {
      // we have to apply some strategy for selecting the best ind from gen0, right now - random
      individual = IndividualHolder.currentIndividual
    }
  }

  override def evaluateState(stateObs: StateObservation): Double = {

    //    bestIndividual = gpRun.getBestIndividual
    val wrappedObservation = new StateObservationWrapper(stateObs)
    individual match {
      case Some(heuristic) => heuristic.run(wrappedObservation)
      case None => throw new RuntimeException("no individual to use for heuristic eval")
    }
  }
}


class ThreadedGPRun() extends Runnable {

  val crossovers = new JavaCodeCrossover(0.3)
  val mutators = List(new ConstantsMutator(0.15), new ForLoopsMutator(0.25), new RegrowMethodMutator(0.15))
  val generations = 100
  val popSize = 32
  val paramTypes = List(new StateObservationWrapper(null))

  val methodCount = 3
  val fitnessCalculator = new SingleGameFitnessCalculator(ThreadedGPRun.gameName)
  //      val fitnessCalculator = new SingleGameFitnessCalculator("zelda")
  //  val fitnessCalculator = new MultiGameFitnessCalculator(cutoff = 2000)
  val selection = new TournamentSelection[JavaCodeIndividual](false)
  val params = new EvolutionParameters[JavaCodeIndividual](fitnessCalculator, selection,
    crossovers, mutators, new RandomGrowInitializer(paramTypes, methodCount), generations, popSize)

  def run() = {

    val logger = CSVEvolutionLogger.createCSVEvolutionLogger[JavaCodeIndividual](getNextLogDirectory("D:\\logs\\"))
    params.setLogger(logger)

    new Run().run(params, new ParentSelectionEvolutionStrategy[JavaCodeIndividual](params))
  }

  def isBestIndividualReady: Boolean = {
    !params.bestIndividual.isEmpty
  }

  def getBestIndividual: Option[JavaCodeIndividual] = {
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
  // works on the fly:
  // works with unlimited time: alians, boulderdash, butterflies, missilecommand, frogs, survivezombies, zelda
  // fails: chase
  // fails compilation/exception:
  // neg infinity?? portals,sokoban

  // cross validation set:
  // works with unlimited time: camelRace, firestorms, infection
  // pass but sucks with unlimited time: digdug, firecaster (but they all fail)
  // fail with unlimited time: overload
  val gameName = "aliens"
  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl" + levelId + ".txt"


  def newInstance: ThreadedGPRun = {
    val run = new ThreadedGPRun()
    val thread = new Thread(run)
    thread.start()
    run
  }

  def main(args: Array[String]): Unit = {
    // create a new threaded GP run, it will update the best individual each gen.
    GPRunHolder.gpRun = ThreadedGPRun.newInstance

    // run a game using the best individual know at each step
    runNewGame()
  }

  def runNewGame() = {
    val gpHeuristic: String = "controllers.heauristicGP.Agent"

    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    Thread.sleep(200) // for the initial bug.
    //Game and level to play
    println("---\nPlaying a game with evolving heuristic")
    val scores = for (i <- 0.to(4)) yield {
      val levelPath = gamesPath + gameName + "_lvl" + i + ".txt"
      ArcadeMachine.runOneGame(gamePath, levelPath, true, gpHeuristic, recordActionsFile, seed)
    }
  }
}
