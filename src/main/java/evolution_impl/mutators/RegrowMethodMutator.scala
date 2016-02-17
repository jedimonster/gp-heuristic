package evolution_impl.mutators

import bgu.cs.evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.{HeuristicsNumbers, RandomGrowInitializer, JavaCodeIndividual}
import japa.parser.ast.body.{BodyDeclaration, MethodDeclaration, ClassOrInterfaceDeclaration}

import scala.collection.mutable
import scala.util.Random

//import scalaj.collection.Imports._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * Created by itayaza on 07/01/2015.
 */
class RegrowMethodMutator(probability: Double) extends Mutator[JavaCodeIndividual] {
  override def mutate(individuals: java.util.List[JavaCodeIndividual]): java.util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      if (Math.random < probability) {
        val visitor: ASTVisitor[JavaCodeIndividual] = new RegrowMethodVisitor()
        visitor.visit(individual.ast, individual)
      }
    }
    inds.asJava
  }

  override def getProbability = probability
}

class GrowNewwMethodMutator(probability: Double) extends Mutator[JavaCodeIndividual] {
  override def mutate(individuals: java.util.List[JavaCodeIndividual]): java.util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      if (Math.random < probability) {
        val visitor: ASTVisitor[JavaCodeIndividual] = new GrowNewMethodVisitor()
        visitor.visit(individual.ast, individual)
      }
    }
    inds.asJava
  }

  override def getProbability = probability
}

class DropMethodMutator(probability: Double) extends Mutator[JavaCodeIndividual] {
  override def mutate(individuals: java.util.List[JavaCodeIndividual]): java.util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      if (Math.random < probability) {
        val visitor: ASTVisitor[JavaCodeIndividual] = new DropMethodVisitor()
        visitor.visit(individual.ast, individual)
      }
    }
    inds.asJava
  }

  override def getProbability = probability
}

class DropMethodVisitor extends ASTVisitor[JavaCodeIndividual] {
  override def visit(n: ClassOrInterfaceDeclaration, individual: JavaCodeIndividual): Unit = {
    super.visit(n, individual)
    val gardener: RandomGrowInitializer = individual.gardener.get
    val membersNoRun: mutable.Buffer[BodyDeclaration] = n.getMembers.filterNot(bd => bd.asInstanceOf[MethodDeclaration].getName.equals("run"))
    if (membersNoRun.size > 1) {
      n.getMembers.remove(n.getMembers.indexOf(membersNoRun(Random.nextInt(membersNoRun.size))))
      gardener.growRunMethod(individual)
    }

  }
}

class GrowNewMethodVisitor extends ASTVisitor[JavaCodeIndividual] {
  override def visit(n: ClassOrInterfaceDeclaration, individual: JavaCodeIndividual): Unit = {
    super.visit(n, individual)
    val membersNoRun: mutable.Buffer[BodyDeclaration] = n.getMembers.filterNot(bd => bd.asInstanceOf[MethodDeclaration].getName.equals("run"))
    // add a new one:
    val gardener: RandomGrowInitializer = individual.gardener.get
    val method: MethodDeclaration = gardener.growMethod(HeuristicsNumbers.getNext, gardener.ParamCount, individual)

    n.getMembers.add(method)
    gardener.growRunMethod(individual)
  }
}

class RegrowMethodVisitor extends ASTVisitor[JavaCodeIndividual] {
  override def visit(n: ClassOrInterfaceDeclaration, individual: JavaCodeIndividual): Unit = {
    super.visit(n, individual)
    // drop a random method
    val membersNoRun: mutable.Buffer[BodyDeclaration] = n.getMembers.filterNot(bd => bd.asInstanceOf[MethodDeclaration].getName.equals("run"))
    n.getMembers.remove(n.getMembers.indexOf(membersNoRun(Random.nextInt(membersNoRun.size))))
    //    n.getMembers.remove(Random.nextInt(n.getMembers.size))
    // add a new one:
    val gardener: RandomGrowInitializer = individual.gardener.get
    val method: MethodDeclaration = gardener.growMethod(HeuristicsNumbers.getNext, gardener.ParamCount, individual)
    n.getMembers.add(method)
    gardener.growRunMethod(individual)
  }
}
