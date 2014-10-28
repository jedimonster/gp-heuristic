package evolution_impl.gpprograms

import java.util

import evolution_engine.evolution.PopulationInitializer

/**
 * Created by itayaza on 28/10/2014.
 * Randomly grows methodCount methods that randomly use parameters from the given list of parameters
 * Combines into a linear combination of resulting numbers.
 */
class RandomGrowInitializer(params: List[Object], methodCount: Int) extends PopulationInitializer[JavaCodeIndividual] {
  override def getInitialPopulation: util.List[JavaCodeIndividual] = ???
}
