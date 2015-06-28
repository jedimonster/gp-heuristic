package controllers.heauristicGP

import core.game.StateObservation
import evolution_impl.gpprograms.base.HeuristicIndividual
import ontology.Types.ACTIONS
import tools.ElapsedCpuTimer

/**
 * Created By Itay Azaria
 * Date: 19/05/2015
 */
class StaticHeuristicAgent(heuristicIndividual: HeuristicIndividual) extends Agent {
  override def act(stateObs: StateObservation, elapsedTimer: ElapsedCpuTimer): ACTIONS = super.act(stateObs, elapsedTimer)
}
