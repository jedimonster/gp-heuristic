package evolution_impl

import java.io.File
import java.nio.file.Path
import java.util.Random

import controllers.Heuristics.StateHeuristic
import core.ArcadeMachine
import core.game.StateObservation
import evolution_engine.selection.TournamentSelection
import evolution_engine.{CSVEvolutionLogger, EvolutionRun}
import evolution_engine.evolution.{EvolutionParameters, ParentSelectionEvolutionStrategy}
import evolution_impl.crossover.{InTreeCrossoverAdapter, JavaCodeCrossover}
import evolution_impl.fitness.{IndividualHolder, MultiGameFitnessCalculator, SingleGameFitnessCalculator}
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.HeuristicIndividual
import evolution_impl.gpprograms.trees.{HeuristicTreeIndividual, HeuristicTreeInitializer}
import evolution_impl.mutators.trees.{RegrowHeuristicMutator, InTreeMutatorAdapter}
import evolution_impl.mutators.{RegrowMethodMutator, ForLoopsMutator, ConstantsMutator}

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
      while (individual.isEmpty && fitness.IndividualHolder.currentIndividual.isEmpty)
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
    val wrappedObservation = new StateObservationWrapper(stateObs)
    individual match {
      case Some(heuristic) => heuristic.run(wrappedObservation)
      case None => throw new RuntimeException("no individual to use for heuristic eval")
    }
  }
}


class ThreadedGPRun() extends Runnable {

  val crossovers = new InTreeCrossoverAdapter(new JavaCodeCrossover(1.0), 0.3)
  val mutators = List(new ConstantsMutator(0.15), new ForLoopsMutator(0.25), new RegrowMethodMutator(0.15))
  val treeMutators = List(new InTreeMutatorAdapter(0.5, mutators), new RegrowHeuristicMutator(0.2))

  val generations = 100
  val popSize = 32
  val paramTypes = List(new StateObservationWrapper(null))

  val methodCount = 3
  val fitnessCalculator = new SingleGameFitnessCalculator[HeuristicTreeIndividual](ThreadedGPRun.gameName)
  //      val fitnessCalculator = new SingleGameFitnessCalculator("zelda")
  //  val fitnessCalculator = new MultiGameFitnessCalculator(cutoff = 2000)
  val selection = new TournamentSelection[HeuristicTreeIndividual](false)

  val params = new EvolutionParameters[HeuristicTreeIndividual](fitnessCalculator, selection,
    crossovers, treeMutators, new HeuristicTreeInitializer(paramTypes, methodCount, 1), generations, popSize)
//  val params = new EvolutionParameters[HeuristicIndividual](fitnessCalculator, selection,
//    crossovers, mutators, new RandomGrowInitializer(paramTypes, methodCount), generations, popSize)

  var runningEvolution: EvolutionRun[HeuristicTreeIndividual] = null

  def run() = {
    val logger = CSVEvolutionLogger.createCSVEvolutionLogger[HeuristicTreeIndividual](getNextLogDirectory("D:\\logs\\"))
    params.setLogger(logger)

    runningEvolution = new EvolutionRun()
    runningEvolution.run(params, new ParentSelectionEvolutionStrategy[HeuristicTreeIndividual](params))
  }

  def stop() = {
    runningEvolution.stop()
  }

  def isBestIndividualReady: Boolean = {
    !params.bestIndividual.isEmpty
  }

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
  // works with unlimited time: alians, boulderdash, butterflies, missilecommand, frogs, survivezombies, zelda
  // fails: chase
  // fails compilation/exception:
  // neg infinity?? portals,sokoban

  // cross validation set:
  // works with unlimited time: camelRace, firestorms, infection
  // pass but sucks with unlimited time: digdug, firecaster (but they all fail)
  // fail with unlimited time: overload
  val gameName = "frogs"

  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  //  val gamePath = gamesPath + gameName + ".txt"
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
//    Thread.sleep(1000)

    // run a game using the best individual know at each step
    runNewGame()
    println("finished playing, stopping evolution...")
    GPRunHolder.gpRun.stop()
  }

  def runNewGame(gameToPlay: String = gameName) = {
    val gpHeuristic: String = "controllers.heauristicGP.Agent"
    val gamePath = gamesPath + gameToPlay + ".txt"

    //Other settings
    val recordActionsFile: String = null
    val seed: Int = new Random().nextInt

    Thread.sleep(1000) // for the initial bug.
    //Game and level to play
    println("---\nPlaying a game with evolving heuristic")
    val scores = for (i <- 0.to(4)) yield {
      val levelPath = gamesPath + gameToPlay + "_lvl" + i + ".txt"
      ArcadeMachine.runOneGame(gamePath, levelPath, true, gpHeuristic, recordActionsFile, seed)
    }

    printf("Scores for %s: %s\n", gameToPlay, scores.toString)
    printf("Average for %s: %s\n", gameToPlay, scores.sum / scores.size)
  }
}
