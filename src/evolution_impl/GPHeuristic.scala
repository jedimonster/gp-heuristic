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
import evolution_impl.fitness.{MultiGameFitnessCalculator, SingleGameFitnessCalculator}
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.{RandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.mutators.{RegrowMethodMutator, ForLoopsMutator, ConstantsMutator}

/**
 * Created by itayaza on 24/11/2014.
 */

object GPRunHolder {
  var gpRun: ThreadedGPRun = null
}

class GPHeuristic(individual: JavaCodeIndividual = null) extends StateHeuristic {
  val gpRun: ThreadedGPRun = GPRunHolder.gpRun

  override def evaluateState(stateObs: StateObservation): Double = {
    var bestIndividual: JavaCodeIndividual = null
    //
    Thread.sleep(40) // because we're allowed 40ms.
    if (gpRun.isBestIndividualReady) {
      bestIndividual = gpRun.getBestIndividual
//      System.out.println("GPHeuristic - using best individual " + bestIndividual.getName)
    } else {
      bestIndividual = fitness.CurrentIndividualHolder.individual
//      System.out.println("GPHeuristic - using pretty random individual " + bestIndividual.getName)
    }
    //    bestIndividual = gpRun.getBestIndividual
    val wrappedObservation = new StateObservationWrapper(stateObs)
    bestIndividual.run(wrappedObservation)
  }
}


class ThreadedGPRun() extends Runnable {

  val crossovers = new JavaCodeCrossover(0.3)
  val mutators = List(new ConstantsMutator(0.15), new ForLoopsMutator(0.25), new RegrowMethodMutator(0.15))
  val generations = 100
  val popSize = 64
  val paramTypes = List(new StateObservationWrapper(null))

  val methodCount = 6
  val fitnessCalculator = new SingleGameFitnessCalculator("aliens")
  //    val fitnessCalculator = new SingleGameFitnessCalculator("frogs")
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

  def getBestIndividual: JavaCodeIndividual = {
    params.synchronized {
      while (params.bestIndividual.isEmpty) {
        params.wait()
      }
      params.bestIndividual.get.asInstanceOf[JavaCodeIndividual]
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

    //Game and level to play
    println("---\nPlaying a game with evolving heuristic")
    val scores = for (i <- 0.to(4)) yield {
      val levelPath = gamesPath + gameName + "_lvl" + i + ".txt"
      ArcadeMachine.runOneGame(gamePath, levelPath, false, gpHeuristic, recordActionsFile, seed)
    }
  }
}
