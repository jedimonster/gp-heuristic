package evolution_impl.mutators

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.JavaCodeIndividual

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class DumbMutator(probability: Double) extends Mutator[JavaCodeIndividual] {

  override def getProbability: Double = probability

  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (x: JavaCodeIndividual <- inds if Math.random() > 0.2) {
      //      new ConstantVisitor().visit(x.ast, null)
      val visitor: ConstantVisitor[Object] = new ConstantVisitor[Object]()
      visitor.visit(x.ast, new Object)
    }
    inds.asJava
  }
}
