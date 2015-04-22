package evolution_impl.gpprograms.base

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import javax.tools.{DiagnosticCollector, JavaCompiler, JavaFileObject, ToolProvider}

import evolution_engine.evolution.Individual
import evolution_impl.GPProgram
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.{ClassName, CompilationException}
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
  var gardener: Option[RandomGrowInitializer] = None
  var compiled = false
  var individualObject: Option[Any] = None

  def setName(s: String) = ast.getTypes.get(0).setName(s)

  def this(ast: CompilationUnit, originalFile: File) = this(ast, originalFile, new ClassName(ast.getTypes.get(0).getName, 0))

  //  @throws[CompilationException]("if the individual couldn't be compiled")
  //  def getValues(values: List[StateObservation]) = {
  //    compile()
  //    for (x <- values) yield run(x)
  //  }

  def run(input: StateObservationWrapper): Double = {
    //    val packageName = ast.getPackage.getName
    this.synchronized {
      if (!compiled) {
        compile()
        //      throw new RuntimeException("Individual not compiled before running")
      }
    }
    val instance = individualObject match {
      case Some(individual) => individual.asInstanceOf[GPProgram]
      case None => throw new RuntimeException("Individual compiled but not instantiated (impossible?)")
    }

    val className = ast.getTypes.get(0).getName

    //    val params = new java.util.ArrayList[Object]
    //    params.add(new java.lang.Double(input))
    try {
      instance.run(input)
    } catch {
      case e: Exception =>
        println("Exception " + e.toString + "while running:")
        println(ast.toString)
        e.printStackTrace()
        System.exit(-1)
        Double.NegativeInfinity
    }
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
      println("Failed compiling\n")
      println(diagnostics.getDiagnostics.get(0).toString)
      println(this.ast.toString)
      //      for(d <- diagnostics.getDiagnostics) {
      //        print(d.toString)
      //      }
      //      GPEvolutionLogger.saveBadIndividual(this)
      throw new CompilationException
    } else {
      compiled = true

    }
    //      printf("Compiled class %s successfully\n", className)
    // todo if failed log errors and catch expeption
    var loadedClass: Class[_] = Class.forName(className)
    loadedClass = ClassLoader.getSystemClassLoader.loadClass(loadedClass.getName)
    val instance: GPProgram = loadedClass.newInstance().asInstanceOf[GPProgram]
    individualObject = Some(instance)
  }

  def writeToFile(path: String) = {
    Files.write(Paths.get(path), ast.toString.getBytes(StandardCharsets.UTF_8))
  }

  /**
   * returns a new individual from the current AST.
   * todo this currently parses the current AST as string, it could probably be improved by proper deep copying, which is not supported by the AST library.
   * @return
   */
  override def duplicate: JavaCodeIndividual = {
    val oldClassName: String = ast.getTypes.get(0).getName
    //    val newClassName = incrementNumber(oldClassName)
    val oldSrc = new ByteArrayInputStream(ast.toString.getBytes(StandardCharsets.UTF_8))
    val newAST: CompilationUnit = JavaParser.parse(oldSrc)
    val newName: ClassName = new ClassName(className.name, NameCounter.getNext)
    newAST.getTypes.get(0).setName(String.valueOf(newName.toString()))
    val individual: JavaCodeIndividual = new JavaCodeIndividual(newAST, originalFile, newName)
    individual.gardener = gardener

    individual
  }

  override def getName: java.lang.String = ast.getTypes.get(0).getName

  override def toString: String = ast.toString
}

object NameCounter {
  private var next = 1

  def getNext = {
    this.synchronized {
      next = next + 1
      next
    }
  }
}

