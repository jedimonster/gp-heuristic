package evolution_impl.fitness.dummyagent;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by itayaza on 22/12/2014.
 */
public class StateObservationWrapper {
    protected core.game.StateObservation so;
    // todo add manhattan distance from stuff.
    // todo fitness<->individual mapping in logs
    // todo search the game tree using heuristics up to cutoff.
    // todo give up and use heuristics in MCTS?
    public StateObservationWrapper(core.game.StateObservation so) {
        this.so = so;
    }

//    @GPIgnore
//    public core.game.StateObservation getState() {
//        return so.copy();
//    }
//
//    public double getScoreInMoves(@AllowedValues(values = {"1", "2", "4", "8", "12", "16"}) int moves) {
//        StateObservation copy = so.copy();
//        for (int i = 0; i < moves; i++) {
//            copy.advance(Types.ACTIONS.ACTION_NIL);
//        }
//
//        return copy.getGameScore();
//    }

    //
//    public double getGameScore() {
//        return so.getGameScore();
//    }

    //
    public int getGameTick() {
        return so.getGameTick();
    }

//    public int getBlockSize() {
//        return so.getBlockSize();
//    }

//    public Vector2d getAvatarPosition() {
//        return so.getAvatarPosition();
//    }

    public double getAvatarSpeed() {
        return so.getAvatarSpeed();
    }

//    public Vector2d getAvatarOrientation() {
//        return so.getAvatarOrientati  on();
//    }

//    public double getResourcesCount() {
//        HashMap<Integer, Integer> idCountMap = so.getAvatarResources();
//        double sum = 0;
//
//        for (Integer v : idCountMap.values()) {
//            sum += v;
//        }
//
//        return sum;
//    }

//    public double getNPCCount() {
//        double sum = 0;
//        for (ArrayList<Observation> observations : so.getNPCPositions()) {
//            sum += observations.size();
//        }
//
//        return sum;
//    }

    // game 0 NPCs: category = 3,3 itype = 4,9
    // game 1 NPCs: category = 3,3 itype = 9,10
    // game 2 NPCs: category = 3 itype = 4
    public Iterable<Observation> getNPCsPositions(
//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5"}) int category,
//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}) int itype) {
            @AllowedValues(values = {"3"}) int category,
            @AllowedValues(values = {"4", "9"}) int itype) {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Observation> result = new ArrayList<>();

        for (ArrayList<Observation> observations : so.getNPCPositions(avatarPosition)) {
            for (Observation observation : observations) {
                if (observation.category == category && observation.itype == itype)
                    result.add(observation);
            }
        }

        return result;
    }

    // game 0 immovables: category = 4, itype = 2
    // game 1 immovables: category = 4,4, type = 0,3
    // game 2 immovables: category = 4,4, type = 0,2
    public Iterable<Observation> getImmovablePositions(

//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5"}) int category,
//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}) int itype) {
            @AllowedValues(values = {"4"}) int category,
            @AllowedValues(values = {"2"}) int itype) {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Observation> result = new ArrayList<>();

        for (ArrayList<Observation> observations : so.getImmovablePositions(avatarPosition)) {
            for (Observation observation : observations) {
                if (observation.category == category && observation.itype == itype)
                    result.add(observation);
            }
        }

        return result;
    }


}
