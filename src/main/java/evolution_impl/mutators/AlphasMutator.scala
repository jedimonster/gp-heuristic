package evolution_impl.mutators

import java.util

import bgu.cs.evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.{WildJavaCodeIndividual, JavaCodeIndividual}
import org.apache.commons.math3.distribution.NormalDistribution
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Created by Itay on 8/9/2015.
 */
class AlphasMutator(probability: Double) extends Mutator[JavaCodeIndividual] {
  val dist = new NormalDistribution(0.0, 0.1)



  /**
   * mutates the given features according to their fitness and appropriate strategy.
   *
   * @param features map of haar features and their fitness (between 0 to 1)
   * @return list of the mutated haar features
   */
  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      val i  = individual.asInstanceOf[WildJavaCodeIndividual]
      if (Math.random < probability) {
        val alphaIndex = Random.nextInt(i.alphas.size)
        val newAlpha = i.alphas(alphaIndex) + dist.sample()
        i.alphas.update(alphaIndex, newAlpha)
      }
    }
    inds.asJava
  }

  override def getProbability: Double = probability
}
