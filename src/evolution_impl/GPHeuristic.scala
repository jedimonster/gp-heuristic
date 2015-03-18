package evolution_impl

import java.io.File
import java.nio.file.Path

import controllers.Heuristics.StateHeuristic
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
  val gpRun = GPRunHolder.gpRun

  override def evaluateState(stateObs: StateObservation): Double = {
    var bestIndividual: JavaCodeIndividual = null
    //
    if (fitness.CurrentIndividualHolder.individual == null) {
      bestIndividual = gpRun.getBestIndividual
    } else {
      bestIndividual = fitness.CurrentIndividualHolder.individual
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
  def newInstance: ThreadedGPRun = {
    val run = new ThreadedGPRun()
    val thread = new Thread(run)
    thread.start()
    run
  }

  def main(args: Array[String]): Unit = {
    GPRunHolder.gpRun = ThreadedGPRun.newInstance
  }
}
