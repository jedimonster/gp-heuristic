package individuals

import java.io.{FileReader, File}
import java.nio.charset.Charset
import java.nio.file.{FileSystems, Files}
import evolution_engine.evolution.{Run, ParentSelectionEvolutionStrategy, EvolutionParameters}
import evolution_engine.selection.RankSelectionStrategy
import evolution_impl.mutators.DumbMutator
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit
import scala.collection.JavaConverters._

import collection.JavaConversions._

import evolution_impl.{LoadDirectoryInitializer, JavaCodeCrossover, EquationFitnessCalculator, JavaCodeIndividual}

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
object Test {
  def main(args: Array[String]) {
    val xo = new JavaCodeCrossover(1.0)
    val mutators = List(new DumbMutator(1)).asJava
    val fitnessCalculator: EquationFitnessCalculator = new EquationFitnessCalculator()
    val selectionStrategy: RankSelectionStrategy[JavaCodeIndividual] = new RankSelectionStrategy[JavaCodeIndividual]
    val parameters: EvolutionParameters[JavaCodeIndividual] = new EvolutionParameters[JavaCodeIndividual](fitnessCalculator, selectionStrategy, xo, mutators, LoadDirectoryInitializer)
    new Run[JavaCodeIndividual].run(parameters, new ParentSelectionEvolutionStrategy[JavaCodeIndividual](parameters))
  }
}
