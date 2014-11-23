package evolution_impl

import java.io.File

import evolution_engine.{CSVEvolutionLogger, Run}
import evolution_engine.evolution.{Individual, ParentSelectionEvolutionStrategy, EvolutionParameters}
import evolution_engine.selection.TournamentSelection
import evolution_impl.gpprograms.{RandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.mutators.ConstantsMutator

/**
 * Created By Itay Azaria
 * Date: 11/23/2014
 */
object Driver {
  def main(args: Array[String]): Unit = {
    val crossovers = new JavaCodeCrossover(1.0)
    val mutators = List(new ConstantsMutator(0.05))
    val generations = 50
    val popSize = 128
    val paramTypes = List(0.0) // only para is a double. value is ignored.
    val methodCount = 2
    val params = new EvolutionParameters[JavaCodeIndividual](new EquationFitnessCalculator(), new TournamentSelection[JavaCodeIndividual](true),
      crossovers, mutators, new RandomGrowInitializer(paramTypes, methodCount), generations, popSize)

    val logger = CSVEvolutionLogger.createCSVEvolutionLogger[JavaCodeIndividual](new File("C:\\Users\\Itay\\logs").toPath)
    params.setLogger(logger)

    new Run().run(params,new ParentSelectionEvolutionStrategy[JavaCodeIndividual](params))
  }
}
