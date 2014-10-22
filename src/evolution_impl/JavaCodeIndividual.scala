package evolution_impl

import java.io._
import java.net.{URL, URLClassLoader}
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
import scala.collection.mutable.ListBuffer
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
    //    val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName
    var loadedClass: Class[_] = Class.forName(className)
    loadedClass = ClassLoader.getSystemClassLoader.loadClass(loadedClass.getName)
    val instance: GPProgram[Integer] = loadedClass.newInstance().asInstanceOf[GPProgram[Integer]]

    val params = new java.util.ArrayList[Object]
    params.add(new Integer(input))
    instance.run(params)
  }

  def compile() = {
//        val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName
//        val fullClassName: String = packageName + "." + className
    //    val originalClass: Class[_] = Class.forName(className)

    val srcFile: JavaSourceFromString = new JavaSourceFromString(className, ast.toString)
    val compilationUnits = java.util.Arrays.asList(srcFile)
    val diagnostics = new DiagnosticCollector[JavaFileObject]()

    val task = javaCompiler.getTask(null, null, diagnostics, null, null, compilationUnits)

    //    printf("original class hash = %s\n", originalClass.hashCode())

    val success = task.call()

    if (!success)
      print("Failed compiling")
    else
      printf("Compiled class %s successfully\n", className)
    // todo if failed log errors

    Class.forName(className)
    // reload the newly compiled class:

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


