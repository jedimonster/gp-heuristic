package controllers.sampleonesteplookahead;


import controllers.Heuristics.MinDistanceHeuristic;
import controllers.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

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
        MinDistanceHeuristic heuristic = new MinDistanceHeuristic(stateObs);
        ArrayList<Double> heursticVals = new ArrayList<>();
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {

            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action); // todo there appears to be a bug in the framework which makes the action do nothing!
            double Q = heuristic.evaluateState(stCopy);


            //System.out.println("Action:" + action + " score:" + Q);
            heursticVals.add(Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }


        }

        // System.out.println("====================");
        return bestAction;


    }


}
