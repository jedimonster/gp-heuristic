package evolution_impl

import java.io.File

import evolution_engine.evolution.PopulationInitializer
import evolution_impl.gpprograms.base.JavaCodeIndividual
import japa.parser.JavaParser

import scala.collection.JavaConversions._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
object LoadDirectoryInitializer extends PopulationInitializer[JavaCodeIndividual] {
  override def getInitialPopulation(n: Int): List[JavaCodeIndividual] = {
    val files: List[File] = new File("individuals").listFiles.filter(f => f.getName.endsWith(".java")).toList
    val individuals: List[JavaCodeIndividual] = for (file <- files) yield new JavaCodeIndividual(JavaParser.parse(file), file)
    individuals
  }
}
