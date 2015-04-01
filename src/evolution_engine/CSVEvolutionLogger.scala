package evolution_engine

import java.io.{File, IOException}
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption.{APPEND, CREATE}
import java.nio.file.{StandardOpenOption, Files, OpenOption, Path}

import evolution_engine.evolution.Individual
import evolution_engine.fitness.FitnessResult
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

/**
 * Created by itayaza on 27/10/2014.
 */
object CSVEvolutionLogger {
  @throws(classOf[IOException])
  def createCSVEvolutionLogger[I <: Individual](logDir: Path): CSVEvolutionLogger[I] = {
    Files.createDirectories(logDir)
    Files.createDirectories(logDir.resolve("individuals/"))
    val fitnessFile: File = new File(logDir.toFile, "fitness.csv")
    val statsFile: File = new File(logDir.toFile, "stats.csv")
    fitnessFile.createNewFile
    statsFile.createNewFile
    val statsPath: Path = statsFile.toPath
    Files.write(statsPath, "Mean,Min,Max,Std. Dev\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND)

    new CSVEvolutionLogger[I](logDir, fitnessFile.toPath, statsPath)
  }
}

class CSVEvolutionLogger[I <: Individual] extends EvolutionLogger[I] {
  private final var logDir: Path = null
  private final var fitnessFile: Path = null
  private final var statsFile: Path = null
  protected var generation: Int = 0

  private def this(logDir: Path, fitnessFile: Path, statsFile: Path) {
    this()
    this.logDir = logDir
    this.fitnessFile = fitnessFile
    this.statsFile = statsFile
  }

  def addGeneration(individuals: List[I], fitness: FitnessResult[I]) {
    addGenerationStatistics(individuals, fitness)
    writeIndividualToDisk(individuals)
    try {
      val line: StringBuilder = new StringBuilder
      import scala.collection.JavaConversions._
      for (individual <- individuals) {
        line.append(fitness.getFitness(individual)).append(",")
      }
      line.append("\n")
      Files.write(fitnessFile, line.toString().getBytes(StandardCharsets.UTF_8), APPEND)
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
    generation += 1
  }

  private def writeIndividualToDisk(individuals: List[_ <: Individual]) {
    try {
      val generationDir: Path = logDir.resolve("individuals/" + generation + "/")
      Files.createDirectories(generationDir)
      import scala.collection.JavaConversions._
      for (individual <- individuals) {
        val name: String = individual.getName
        val individualPath: Path = generationDir.resolve(name + ".java")
        Files.write(individualPath, individual.toString.getBytes(StandardCharsets.UTF_8), CREATE)
      }
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  protected def addGenerationStatistics(individuals: List[I], fitness: FitnessResult[I]) {
    val fitnessArray = for (i: I <- individuals) yield fitness.getFitness(i)
    val stats: DescriptiveStatistics = new DescriptiveStatistics(fitnessArray.toArray)
    try {
      val line: String = "Average = %f, Minimum = %f, Maximum = %f, Std. dev. = %f\n".format(stats.getMean, stats.getMin, stats.getMax, stats.getStandardDeviation)
      val csvLine : String = "%f,%f,%f,%f\n".format(stats.getMean, stats.getMin, stats.getMax, stats.getStandardDeviation)
//      System.out.println(line)
      Files.write(statsFile, csvLine.toString.getBytes(StandardCharsets.UTF_8), APPEND)
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}