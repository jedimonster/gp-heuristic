package evolution_impl

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Files, Paths}

import evolution_engine.evolution.Individual
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import scala.sys.process._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeIndividual(
                          val ast: CompilationUnit, val originalFile: File
                          ) extends Individual {
  def getValues(values: List[Int]) = {
    compile()
    for (x <- values) yield run(x)
  }

  def run(input: Int): Int = {
    val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName
    val cmd = "java -cp src " + packageName + "/" + className + " " + input
    print("CLASSLOADER")
    val resultText = new String(cmd !!).trim
    resultText.toInt
  }

  def compile() = {
    val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName
    val javaPath = "src/" + packageName + "/" + className + ".java"
    val compiledPath = packageName + "/" + className + ""

    writeToFile(javaPath)
    val cmd = "javac " + javaPath
    cmd.!
  }

  def writeToFile(path: String) = {
    Files.write(Paths.get(path), ast.toString.getBytes(StandardCharsets.UTF_8))
  }

  override def duplicate(): Individual = {
    // copy the file since we're gonna need a copy either way..
    // todo naming scheme
    val oldClassName: String = ast.getTypes.get(0).getName
    val newClassName = oldClassName + "_"
    new File(originalFile.getParent + "/" + newClassName + "/").mkdir()
    val newFile = new File(originalFile.getParent + "/" + newClassName + "/" + oldClassName + ".java")
    new FileOutputStream(newFile) getChannel() transferFrom(new FileInputStream(originalFile).getChannel, 0, Long.MaxValue)
    val newGuy = JavaParser.parse(newFile)
    new JavaCodeIndividual(newGuy, newFile)
  }
}


