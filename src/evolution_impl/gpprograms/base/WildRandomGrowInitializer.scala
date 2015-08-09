package evolution_impl.gpprograms.base

import java.io.File

import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit
import org.apache.commons.math3.distribution.NormalDistribution

import scala.collection.mutable

/**
 * Created by Itay on 8/9/2015.
 */
class WildRandomGrowInitializer(params: List[Any], methodCount: Int) extends RandomGrowInitializer(params, methodCount) {
  val alphasDist = new NormalDistribution(0.8, 0.05)

  override val prototype: WildJavaCodeIndividual = {
    val prototypeFile: File = new File("individuals/Prototype.java")
    val ast: CompilationUnit = JavaParser.parse(prototypeFile)
    val alphas = mutable.IndexedSeq(alphasDist.sample(), alphasDist.sample())
    new WildJavaCodeIndividual(ast, prototypeFile, alphas)
  }

  override def growIndividual(id: Int): JavaCodeIndividual = {
    val ind = super.growIndividual(id).asInstanceOf[WildJavaCodeIndividual]
    ind.alphas = mutable.IndexedSeq(alphasDist.sample(), alphasDist.sample())
    ind
  }
}
