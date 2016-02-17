package evolution_engine.evolution

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
trait PopulationInitializer[+I <: Individual] {
  def getInitialPopulation(n: Int): List[I]
}