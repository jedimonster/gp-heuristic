package evolution_impl

import java.io.{FilenameFilter, File}
import java.util

import evolution_engine.evolution.PopulationInitializer
import japa.parser.JavaParser
import scala.collection.JavaConversions._

import scala.collection.JavaConverters._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
object LoadDirectoryInitializer extends PopulationInitializer[JavaCodeIndividual] {
  override def getInitialPopulation: java.util.List[JavaCodeIndividual] = {
    val files: List[File] = new File("src/individuals").listFiles.filter(f => f.getName.endsWith(".java")).toList
    val individuals: List[JavaCodeIndividual] = for (file <- files) yield new JavaCodeIndividual(JavaParser.parse(file), file)
    new java.util.ArrayList[JavaCodeIndividual](individuals)
  }
}
