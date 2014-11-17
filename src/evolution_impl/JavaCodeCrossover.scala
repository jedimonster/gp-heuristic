package evolution_impl

import java.util
import java.util.Collections

import evolution_engine.mutators.Crossover
import evolution_impl.gpprograms.JavaCodeIndividual
import japa.parser.ast.body.BodyDeclaration

import scala.collection.JavaConverters._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeCrossover(probability: Double) extends Crossover[JavaCodeIndividual] {
  override def cross(father: JavaCodeIndividual, mother: JavaCodeIndividual): java.util.List[JavaCodeIndividual] = {
    val son: JavaCodeIndividual = father.duplicate().asInstanceOf[JavaCodeIndividual]
    val daughter: JavaCodeIndividual = father.duplicate().asInstanceOf[JavaCodeIndividual]
    val sonMembers: java.util.List[BodyDeclaration] = son.ast.getTypes.get(0).getMembers
    val daughterMembers: java.util.List[BodyDeclaration] = son.ast.getTypes.get(0).getMembers
    val n = sonMembers.size()

    // get the run() method out of the list - we'll have to recreate it anyway.

    // shuffle to avoid position bias.
    Collections.shuffle(sonMembers)
    Collections.shuffle(daughterMembers)

    // add 1/2 of each to the other, then drop the first 1/2 (now a 1/3).
    for(i <- 0 to n/2) {
      sonMembers.add(daughterMembers.get(i))
      daughterMembers.add(sonMembers.get(i))
    }

    for(i <- 0 to n/2) {
      sonMembers.remove(i)
      daughterMembers.remove(i)
    }

    // rename the bastards.

    // create new run methods.

    List(father, mother).asJava
  }

  override def getProbability: Double = probability
}
