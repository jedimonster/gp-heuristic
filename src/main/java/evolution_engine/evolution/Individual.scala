package evolution_engine.evolution

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
trait Individual {
  def duplicate: Individual

  def getName: String
}