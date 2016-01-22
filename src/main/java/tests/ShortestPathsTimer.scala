//package tests
//
//import core.game.{StateObservation, Game}
//import core.{VGDLParser, VGDLRegistry, VGDLFactory}
//import evolution_impl.fitness.dummyagent.StateObservationWrapper
//import evolution_impl.search.{Position, GraphCachingAStar}
//
///**
// * Created By Itay Azaria
// * Date: 21/07/2015
// */
//object ShortestPathsTimer {
//
//  def getNewStateObservation(): StateObservationWrapper = {
//    val gamesPath: String = "gvgai/examples/gridphysics/"
//    val levelId = 1
//    val gameName = "pacman"
//    val gamePath = gamesPath + gameName + ".txt"
//    val levelPath = gamesPath + gameName + "_lvl"
//    //  + levelId + ".txt"
//    val vGDLFactory = VGDLFactory.GetInstance().init()
//    val vGDLRegistry = VGDLRegistry.GetInstance().init()
//    val game: Game = new VGDLParser().parseGame(gamePath)
//    game.buildLevel(levelPath + "0" + ".txt")
//
//    val stateObs: StateObservation = game.getObservation.copy()
//    val blockSize: Int = stateObs.getBlockSize
//    val avatarPosition = stateObs.getAvatarPosition
//    val graphRoot: Position = new Position(avatarPosition.x.toInt / blockSize, avatarPosition.y.toInt / blockSize, stateObs)
//    new StateObservationWrapper(stateObs, new GraphCachingAStar[Position](graphRoot))
//  }
//
//  def main(args: Array[String]): Unit = {
//    val stateWrapper = getNewStateObservation()
//
//    var before = System.nanoTime()
//    var distances = stateWrapper.getImmovableRealDistance
//    var after = System.nanoTime()
//    printf("time for 1 run = %dns, %fms\n", after - before, (after - before).asInstanceOf[Double] / 1000000)
//
//    before = System.nanoTime()
//    distances = stateWrapper.getNPCRealDistance
//    after = System.nanoTime()
//    printf("time for 1 run = %dns, %fms\n", after - before, (after - before).asInstanceOf[Double] / 1000000)
//  }
//}
