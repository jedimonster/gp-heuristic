package evolution_impl.fitness.dummyagent;

import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by itayaza on 22/12/2014.
 */
public class StateObservationWrapper {
    protected StateObservation so;

    public StateObservationWrapper(StateObservation so) {
        this.so = so;
    }

    public double getGameScore() {
        return so.getGameScore();
    }

    public int getGameTick() {
        return so.getGameTick();
    }

    public int getBlockSize() {
        return so.getBlockSize();
    }

    public Vector2d getAvatarPosition() {
        return so.getAvatarPosition();
    }

    public double getAvatarSpeed() {
        return so.getAvatarSpeed();
    }

    public Vector2d getAvatarOrientation() {
        return so.getAvatarOrientation();
    }

    public double getResourcesCount() {
        HashMap<Integer, Integer> idCountMap = so.getAvatarResources();
        double sum = 0;

        for (Integer v : idCountMap.values()) {
            sum += v;
        }

        return sum;
    }

    public double getNPCCount() {
        double sum = 0;
        for (ArrayList<Observation> observations : so.getNPCPositions()) {
            sum += observations.size();
        }

        return sum;
    }

}
