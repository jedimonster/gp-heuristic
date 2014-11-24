import java.io.File
import java.nio.file.Path

import evolution_engine.{Run, CSVEvolutionLogger}
import evolution_engine.evolution.{ParentSelectionEvolutionStrategy, EvolutionParameters}
import evolution_engine.selection.{TournamentSelection}
import evolution_impl.gpprograms.{RandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.{LoadDirectoryInitializer, EquationFitnessCalculator, JavaCodeCrossover}
import evolution_impl.mutators.{CreateMathExpMutator, ConstantsMutator}

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
object Test {
  def main(args: Array[String]) {
    val xo = new JavaCodeCrossover(1.0)
    val mutators = List(new ConstantsMutator(0.05)
      //      , new CreateMathExpMutator(1)
    )
    val fitnessCalculator: EquationFitnessCalculator = new EquationFitnessCalculator()
    val selectionStrategy: TournamentSelection[JavaCodeIndividual] = new TournamentSelection[JavaCodeIndividual](true)
    var params = List(2.0)

    val initializer = new RandomGrowInitializer(params, 3)
    val generations: Int = 50
    val popSize: Int = 64
    val parameters: EvolutionParameters[JavaCodeIndividual] = new EvolutionParameters[JavaCodeIndividual](fitnessCalculator, selectionStrategy, xo, mutators, initializer, generations, popSize)
    val logger: CSVEvolutionLogger[JavaCodeIndividual] = CSVEvolutionLogger.createCSVEvolutionLogger[JavaCodeIndividual](getNextLogDirectory("D:\\logs\\"))

    parameters.setLogger(logger)

    new Run[JavaCodeIndividual].run(parameters, new ParentSelectionEvolutionStrategy[JavaCodeIndividual](parameters))
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
