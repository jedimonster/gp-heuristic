package bgu.cs.evolution_engine.evolution

/**
 * Created by Itay Azaria
 * Date: 28/03/14
 * Time: 16:28
 */
trait EvolutionStrategy[I <: Individual] {
  def evolve(individuals: List[I]): List[I]
}