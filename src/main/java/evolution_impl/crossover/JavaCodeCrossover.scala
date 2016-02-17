package evolution_impl.crossover

import java.util.{ArrayList, LinkedList}

import bgu.cs.evolution_engine.mutators.Crossover
import evolution_impl.gpprograms.base.{RandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.gpprograms.TreeGrowingException
import japa.parser.ast.TypeParameter
import japa.parser.ast.`type`.ClassOrInterfaceType
import japa.parser.ast.body.{BodyDeclaration, MethodDeclaration, Parameter}
import japa.parser.ast.expr.NameExpr
import japa.parser.ast.stmt.{BlockStmt, Statement}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeCrossover(probability: Double) extends Crossover[JavaCodeIndividual] {

  def createRunMethod(individual: JavaCodeIndividual) = {
    if (individual.gardener.isEmpty) {
      throw new TreeGrowingException("Can't find gardener for " + individual.toString)
    }
    val gardener: RandomGrowInitializer = individual.gardener.get
    gardener.growRunMethod(individual)
  }

  def renameMethods(individual: JavaCodeIndividual) = {
    val methods = individual.ast.getTypes.get(0).getMembers.toList
    var i = 0
    for (method <- methods) {
      method match {
        case declaration: MethodDeclaration =>
          // no need since names are now unique.
//          declaration.setName("heuristic" + i.toString)
          i += 1
        case _ =>
          print("frak")
      }
    }
    individual.ast.getTypes.get(0).setMembers(methods)
  }

  def copyMethod(other: BodyDeclaration): MethodDeclaration = {
    other match {
      case other: MethodDeclaration =>
        val parameters: ArrayList[TypeParameter] = if (other.getTypeParameters == null) null else new ArrayList(other.getTypeParameters)
        val value: ArrayList[Parameter] = if (other.getParameters == null) null else new ArrayList[Parameter](other.getParameters)
        val exprs: LinkedList[NameExpr] = if (other.getThrows == null) null else new LinkedList[NameExpr](other.getThrows)
        val statements: ArrayList[Statement] = if (other.getBody.getStmts == null) null else new ArrayList[Statement](other.getBody.getStmts)
        val method = new MethodDeclaration(other.getJavaDoc, other.getModifiers, other.getAnnotations, parameters, new ClassOrInterfaceType(other.getType.toString), other.getName, value, other.getArrayCount, exprs, new BlockStmt(statements))

        method
      case _ => throw new TreeGrowingException("trying to copy none method")
    }
  }

  override def cross(father: JavaCodeIndividual, mother: JavaCodeIndividual): List[JavaCodeIndividual] = {
    val son: JavaCodeIndividual = father.duplicate
    val daughter: JavaCodeIndividual = father.duplicate
    var sonMembers = son.ast.getTypes.get(0).getMembers.asScala
    var daughterMembers = son.ast.getTypes.get(0).getMembers.asScala
    val n = sonMembers.size()

    // get the run() method out of the list - we'll have to recreate it anyway.

    // shuffle to avoid position bias.
    sonMembers = Random.shuffle(sonMembers)
    daughterMembers = Random.shuffle(daughterMembers)

    // add 1/2 of each to the other, then drop the first 1/2 (now a 1/3).
    for (i <- 0 to n / 2) {
      sonMembers.add(daughterMembers.get(i))
      daughterMembers.add(sonMembers.get(i))
    }

    for (i <- 0 to n / 2) {
      sonMembers.remove(i)
      daughterMembers.remove(i)
    }

    // remove old run method
    son.gardener.get.removeRunMethod(son)
    daughter.gardener.get.removeRunMethod(daughter)

    // rename the other methods.
    renameMethods(son)
    renameMethods(daughter)

    // create new run methods.
    createRunMethod(son)
    createRunMethod(daughter)

    List(daughter, son)
  }

  override def getProbability: Double = probability
}
