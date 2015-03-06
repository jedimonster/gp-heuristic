import core.game.Game
import core.{VGDLParser, VGDLRegistry, VGDLFactory}
import evolution_impl.fitness.SingleGameFitnessCalculator
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.JavaCodeIndividual

/**
 * Created By Itay Azaria
 * Date: 3/5/2015
 */
object Debug {
  val gameName: String = "aliens"
  val gamesPath: String = "gvgai/examples/gridphysics/"
  val levelId = 0
  val gamePath = gamesPath + gameName + ".txt"
  val levelPath = gamesPath + gameName + "_lvl" + levelId + ".txt"


  def main(args: Array[String]): Unit = {
    val ind = new Ind68()
    val f = new SingleGameFitnessCalculator(gameName)
    VGDLFactory.GetInstance.init
    VGDLRegistry.GetInstance.init
    val toPlay: Game = new VGDLParser().parseGame(gamePath)
    toPlay.buildLevel(levelPath)
    val state = toPlay.getObservation

    ind.run(new StateObservationWrapper(state))
  }
}
