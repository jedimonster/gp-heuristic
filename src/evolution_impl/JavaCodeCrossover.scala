package evolution_impl

import scala.collection.JavaConverters._
import evolution_engine.mutators.Crossover

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class JavaCodeCrossover(probability: Double) extends Crossover[JavaCodeIndividual] {
  override def cross(father: JavaCodeIndividual, mother: JavaCodeIndividual): java.util.List[JavaCodeIndividual] = {
    List(father, mother).asJava
  }

  override def getProbability: Double = probability
}
