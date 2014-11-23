package evolution_engine.evolution

import evolution_engine.EvolutionLogger
import evolution_engine.fitness.FitnessCalculator
import evolution_engine.mutators.{Crossover, Mutator}
import evolution_engine.selection.SelectionStrategy

class EvolutionParameters[I <: Individual] {
  private final var fitnessCalculator: FitnessCalculator[I] = null
  private final var selectionStrategy: SelectionStrategy[I] = null
  private final var crossover: Crossover[I] = null
  private final var mutators: List[_ <: Mutator[I]] = null
  private final var populationInitializer: PopulationInitializer[I] = null
  private var populationSize: Int = 0
  private var logger: EvolutionLogger[I] = null
  private var generations: Int = 0

  def this(fitnessCalculator: FitnessCalculator[I], selectionStrategy: SelectionStrategy[I], crossover: Crossover[I], mutators: List[_ <: Mutator[I]], populationInitializer: PopulationInitializer[I], generations: Int, populationSize: Int) {
    this()
    this.fitnessCalculator = fitnessCalculator
    this.selectionStrategy = selectionStrategy
    this.crossover = crossover
    this.mutators = mutators
    this.populationInitializer = populationInitializer
    this.generations = generations
    this.populationSize = populationSize
  }

  def getFitnessCalculator: FitnessCalculator[I] = {
     fitnessCalculator
  }

  def getSelectionStrategy: SelectionStrategy[I] = {
     selectionStrategy
  }

  def getCrossover: Crossover[I] = {
     crossover
  }

  def getMutators: List[Mutator[I]] = {
     mutators
  }

  def getPopulationInitializer: PopulationInitializer[I] = {
     populationInitializer
  }

  def isLoggingEnable: Boolean = {
     logger != null
  }

  def getLogger: EvolutionLogger[I] = {
     logger
  }

  def setLogger(logger: EvolutionLogger[I]) {
    this.logger = logger
  }

  def getPopulationSize: Int = {
     populationSize
  }

  def setPopulationSize(populationSize: Int) {
    this.populationSize = populationSize
  }

  def getGenerations: Int = {
     generations
  }

  def setGenerations(generations: Int) {
    this.generations = generations
  }
}