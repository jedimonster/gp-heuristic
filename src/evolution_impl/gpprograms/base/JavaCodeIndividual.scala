package evolution_impl.gpprograms.base

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import javax.tools.{DiagnosticCollector, JavaCompiler, JavaFileObject, ToolProvider}

import evolution_impl.GPProgram
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.{ClassName, CompilationException}
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit
import org.mdkt.compiler.InMemoryJavaCompiler

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeIndividual(
                          val ast: CompilationUnit, val originalFile: File, val className: ClassName
                          ) extends HeuristicIndividual {
  var gardener: Option[RandomGrowInitializer] = None
  var compiled = false
  var individualObject: Option[Any] = None
  val compiler: InMemoryJavaCompiler = new InMemoryJavaCompiler()

  def setName(s: String) = ast.getTypes.get(0).setName(s)

  //  @throws[CompilationException]("if the individual couldn't be compiled")
  //  def getValues(values: List[StateObservation]) = {
  //    compile()
  //    for (x <- values) yield run(x)
  //  }

  def this(ast: CompilationUnit, originalFile: File) = this(ast, originalFile, new ClassName(ast.getTypes.get(0).getName, 0))

  override def run(input: StateObservationWrapper): Double = {
    //    this.synchronized {
    //      if (!compiled) {
    //        compile()
    //      }
    //    }
    val instance = individualObject match {
      case Some(individual) => individual.asInstanceOf[GPProgram]
      case None => throw new RuntimeException("Individual compiled but not instantiated (impossible?)")
    }

    val className = ast.getTypes.get(0).getName

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
  def compile(): Unit = {
    if (compiled)
      return
    val className = ast.getTypes.get(0).getName
    try {
      val loadedClass = InMemoryJavaCompiler.compile(className, ast.toString())
      compiled = true
      val instance: GPProgram = loadedClass.newInstance().asInstanceOf[GPProgram]
      individualObject = Some(instance)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new CompilationException
    }
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

