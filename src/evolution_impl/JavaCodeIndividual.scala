package evolution_impl

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Files, Paths}

import evolution_engine.evolution.Individual
import evolution_impl.util.JavaSourceFromString
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;
import scala.sys.process._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeIndividual(
                          val ast: CompilationUnit, val originalFile: File
                          ) extends Individual {

  val javaCompiler: JavaCompiler = ToolProvider.getSystemJavaCompiler()

  def getValues(values: List[Int]) = {
    compile()
    for (x <- values) yield run(x)
  }

  def run(input: Int): Int = {
    ??? // todo
  }

  def compile() = {
    val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName

    val srcFile: JavaSourceFromString = new JavaSourceFromString(className, ast.toString)
    val compilationUnits = java.util.Arrays.asList(srcFile)
    val diagnostics = new DiagnosticCollector[JavaFileObject]()
    val task = javaCompiler.getTask(null, null, diagnostics, null, null, compilationUnits)


    //    printf("original class hash = %s", Class.forName(className))

    val success = task.call()
    if (!success)
      print("Failed compiling")
    else
      printf("Compiled class %s successfully\n", className)
    // todo if failed log errors

    val loadedClass: Class[_] = Class.forName(packageName + "." + className)
    printf("reloaded class %s, hash = %s\n", className, loadedClass.hashCode())
  }

  def writeToFile(path: String) = {
    Files.write(Paths.get(path), ast.toString.getBytes(StandardCharsets.UTF_8))
  }

  /**
   * returns a new individual from the current AST.
   * todo this currently parses the current AST as string, it could probably be improved by proper deep copying, which is not supported by the AST library.
   * @return
   */
  override def duplicate(): Individual = {
    val oldClassName: String = ast.getTypes.get(0).getName
    val newClassName = oldClassName + "_"
    val oldSrc = new ByteArrayInputStream(ast.toString.getBytes(StandardCharsets.UTF_8))
    val newGuy = JavaParser.parse(oldSrc)
    new JavaCodeIndividual(newGuy, originalFile)
  }
}


