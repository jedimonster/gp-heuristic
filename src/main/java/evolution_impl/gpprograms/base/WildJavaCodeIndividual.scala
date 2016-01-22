package evolution_impl.gpprograms.base

import java.io.{ByteArrayInputStream, File}
import java.nio.charset.StandardCharsets

import evolution_impl.gpprograms.ClassName
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by Itay on 8/9/2015.
 */
class WildJavaCodeIndividual(ast: CompilationUnit, originalFile: File, var alphas: mutable.IndexedSeq[Double], className: ClassName) extends JavaCodeIndividual(ast, originalFile, className) {
  def this(ast: CompilationUnit, originalFile: File, alphas: mutable.IndexedSeq[Double]) = this(ast, originalFile, alphas, new ClassName(ast.getTypes.get(0).getName, 0))

  /**
   * returns a new individual from the current AST.
   * todo this currently parses the current AST as string, it could probably be improved by proper deep copying, which is not supported by the AST library.
   * @return
   */
  override def duplicate: WildJavaCodeIndividual = {
    val oldClassName: String = ast.getTypes.get(0).getName
    //    val newClassName = incrementNumber(oldClassName)
    val oldSrc = new ByteArrayInputStream(ast.toString.getBytes(StandardCharsets.UTF_8))
    val newAST: CompilationUnit = JavaParser.parse(oldSrc)
    val newName: ClassName = new ClassName(className.name, NameCounter.getNext)
    newAST.getTypes.get(0).setName(String.valueOf(newName.toString()))
    val individual: WildJavaCodeIndividual = new WildJavaCodeIndividual(newAST, originalFile, alphas, newName)
    individual.gardener = gardener

    individual
  }
}
