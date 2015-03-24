package evolution_impl.fitness.dummyagent;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by itayaza on 22/12/2014.
 */
public class StateObservationWrapper {
    protected core.game.StateObservation so;

    // todo add manhattan distance from stuff.
    // todo fitness<->individual mapping in logs
    // todo add random loops to gen 0
    // todo search the game tree using heuristics up to cutoff.
    // perhaps look at top x, or all until there's actually variance/cutoff time.
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
    public double getGameScore() {
        return so.getGameScore();
    }

    //
    public int getGameTick() {
        return so.getGameTick();
    }

    public int getBlockSize() {
        return so.getBlockSize();
    }

//    public Vector2d getAvatarPosition() {
//        return so.getAvatarPosition();
//    }

    public double getAvatarSpeed() {
        return so.getAvatarSpeed();
    }

//    public Vector2d getAvatarOrientation() {
//        return so.getAvatarOrientati  on();
//    }

    public double getResourcesCount() {
        HashMap<Integer, Integer> idCountMap = so.getAvatarResources();
        double sum = 0;

        for (Integer v : idCountMap.values()) {
            sum += v;
        }

        return sum;
    }

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

        ArrayList<Observation>[] npcPositions = so.getNPCPositions(avatarPosition);
        if (npcPositions != null) {
            for (ArrayList<Observation> observations : npcPositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    result.add(observation);
                }
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

        ArrayList<Observation>[] immovablePositions = so.getImmovablePositions(avatarPosition);
        if (immovablePositions != null) {
            for (ArrayList<Observation> observations : immovablePositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    result.add(observation);
                }
            }
        }

        return result;
    }

    public Iterable<Double> getResourcesHeuristicDistance() {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Double> result = new ArrayList<>();

        ArrayList<Observation>[] resourcesPositions = so.getResourcesPositions(avatarPosition);
        if (resourcesPositions != null) {
            for (ArrayList<Observation> observations : resourcesPositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    double distance = observation.sqDist + countBlockingWalls(so, observation);

                    result.add(distance);
                }
            }
        }

        return result;
    }


    public Iterable<Double> getPortalsHeuristicDistance() {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Double> result = new ArrayList<>();

        ArrayList<Observation>[] portalsPositions = so.getPortalsPositions(avatarPosition);
        if (portalsPositions != null) {
            for (ArrayList<Observation> observations : portalsPositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    double distance = observation.sqDist + countBlockingWalls(so, observation);

                    result.add(distance);
                }
            }
        }

        return result;
    }

    public Iterable<Double> getNPCHeursticDistance() {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Double> result = new ArrayList<>();

        ArrayList<Observation>[] npcPositions = so.getNPCPositions(avatarPosition);
        if (npcPositions != null) {
            for (ArrayList<Observation> observations : npcPositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    double distance = observation.sqDist + countBlockingWalls(so, observation);

                    result.add(distance);
                }
            }
        }

        return result;
    }

    @GPIgnore
    private double countBlockingWalls(StateObservation stateObservation, Observation dstObservation) {
        int walls = 0;
        ArrayList<Observation>[][] observationGrid = stateObservation.getObservationGrid();
        int currentX = (int) stateObservation.getAvatarPosition().x / stateObservation.getBlockSize(),
                currentY = (int) stateObservation.getAvatarPosition().y / stateObservation.getBlockSize();

        int dstX = (int) (dstObservation.position.x / stateObservation.getBlockSize());
        int dstY = (int) (dstObservation.position.y / stateObservation.getBlockSize());
        while (currentX != dstX || currentY != dstY) {
            if (!observationGrid[currentX][currentY].isEmpty()) // something is there
                walls++;
            if (dstX > currentX)
                currentX++;
            else if (dstX < currentX)
                currentX--;

            if (dstY > currentY)
                currentY++;
            else if (dstY < currentY)
                currentY--;
        }


        return walls;
    }


}
