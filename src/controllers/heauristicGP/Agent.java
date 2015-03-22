package controllers.heauristicGP;


import core.game.StateObservation;
import core.player.AbstractPlayer;
import evolution_impl.GPHeuristic;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    protected GPHeuristic heuristic;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        heuristic = new GPHeuristic(null);
        heuristic.waitForFirstIndividual(); // this will wait until we have an individual
    }

    /**
     * Very simple one step lookahead agent.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {

            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);
            double Q = heuristic.evaluateState(stCopy);


            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }
        while (elapsedTimer.remainingTimeMillis() > 15) {
        }

//        System.out.println("====================");
//        System.out.printf("chose action %s in %dms\n", bestAction, elapsedTimer.elapsedMillis());
        return bestAction;


    }


}
