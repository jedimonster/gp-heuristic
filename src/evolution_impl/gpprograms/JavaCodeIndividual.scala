package evolution_impl.gpprograms

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import javax.tools.{DiagnosticCollector, JavaCompiler, JavaFileObject, ToolProvider}

import evolution_engine.evolution.Individual
import evolution_impl.GPProgram
import evolution_impl.log.GPEvolutionLogger
import evolution_impl.util.JavaSourceFromString
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeIndividual(
                          val ast: CompilationUnit, val originalFile: File, val className: ClassName
                          ) extends Individual {
  val javaCompiler: JavaCompiler = ToolProvider.getSystemJavaCompiler()

  def setName(s: String) = ast.getTypes.get(0).setName(s)

  def this(ast: CompilationUnit, originalFile: File) = this(ast, originalFile, new ClassName(ast.getTypes.get(0).getName, 0))

  @throws[CompilationException]("if the individual couldn't be compiled")
  def getValues(values: List[Double]) = {
    compile()
    for (x <- values) yield run(x)
  }

  def run(input: Double): Double = {
    //    val packageName = ast.getPackage.getName
    val className = ast.getTypes.get(0).getName
    var loadedClass: Class[_] = Class.forName(className)
    loadedClass = ClassLoader.getSystemClassLoader.loadClass(loadedClass.getName)
    val instance: GPProgram[java.lang.Double] = loadedClass.newInstance().asInstanceOf[GPProgram[java.lang.Double]]

    //    val params = new java.util.ArrayList[Object]
    //    params.add(new java.lang.Double(input))
    instance.run(input)
  }

  @throws[CompilationException]("if the individual couldn't be compiled")
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

    if (!success) {
      print("Failed compiling\n")
      GPEvolutionLogger.saveBadIndividual(this)
      throw new CompilationException
    } else
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
    //    val newClassName = incrementNumber(oldClassName)
    val oldSrc = new ByteArrayInputStream(ast.toString.getBytes(StandardCharsets.UTF_8))
    val newAST: CompilationUnit = JavaParser.parse(oldSrc)
    val newName: ClassName = new ClassName(className.name, NameCounter.getNext())
    newAST.getTypes.get(0).setName(String.valueOf(newName.toString()))
    new JavaCodeIndividual(newAST, originalFile, newName)
  }
}

object NameCounter {
  var next = 1

  def getNext() = {
    next = next + 1
    next
  }
}

