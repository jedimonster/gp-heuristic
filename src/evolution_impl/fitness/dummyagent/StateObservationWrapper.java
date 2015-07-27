package evolution_impl.fitness.dummyagent;

import core.game.Observation;
import core.game.StateObservation;
import evolution_impl.fitness.IndividualHolder;
import evolution_impl.fitness.IndividualHolder$;
import evolution_impl.search.AStar;
import evolution_impl.search.AStarException;
import evolution_impl.search.AStarPathRequest;
import evolution_impl.search.Position;
import ontology.Types;
import tools.Vector2d;

import java.util.*;

/**
 * Created by itayaza on 22/12/2014.
 */
public class StateObservationWrapper {
    protected AStar<Position> aStar;
    protected core.game.StateObservation so;

    // todo fitness<->individual mapping in logs
    // perhaps look at top x, or all until there's actually variance/cutoff time.
    public StateObservationWrapper(core.game.StateObservation so) {
        this(so, IndividualHolder.aStar());
    }

    public StateObservationWrapper(StateObservation so, AStar<Position> aStar) {
        this.so = so;
        this.aStar = aStar;
    }

//    @GPIgnore
//    public core.game.StateObservation getState() {
//        return so.copy();
//    }
//

    @GPIgnore
    public double getScoreInMoves(@AllowedValues(values = {"1", "2", "4"}) int moves) {
        StateObservation copy = so.copy();
        for (int i = 0; i < moves; i++) {
            copy.advance(Types.ACTIONS.ACTION_NIL);
        }

        return copy.getGameScore();
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

    public double countTouchingNPCs() {
        double oneBlockSqDistance = Math.pow(so.getBlockSize(), 2); // the square distance from a touching npc should be equal to this.
        List<Observation> npcPositions = flatObservations(so.getNPCPositions(so.getAvatarPosition()));
        double count = 0;

        for (Observation npcPosition : npcPositions) {
            if (npcPosition.sqDist <= oneBlockSqDistance)
                count++;
        }

        return count;
    }

    public double getAvatarResourcesCount() {
        HashMap<Integer, Integer> idCountMap = so.getAvatarResources();
        double sum = 0;

        for (Integer v : idCountMap.values()) {
            sum += v;
        }

        return sum;
    }

    //
    public double getNPCCount() {
        double sum = 0;
        ArrayList<Observation>[] npcPositions = so.getNPCPositions();
        if (npcPositions != null) {
            for (ArrayList<Observation> observations : npcPositions) {
                sum += observations.size();
            }
        }
        return sum;
    }

    // game 0 NPCs: category = 3,3 itype = 4,9
    // game 1 NPCs: category = 3,3 itype = 9,10
    // game 2 NPCs: category = 3 itype = 4
//    public Iterable<Observation> getNPCsPositions(
////            @AllowedValues(values = {"0", "1", "2", "3", "4", "5"}) int category,
////            @AllowedValues(values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}) int itype) {
//            @AllowedValues(values = {"3"}) int category,
//            @AllowedValues(values = {"4", "9"}) int itype) {
//        Vector2d avatarPosition = so.getAvatarPosition();
//
//        ArrayList<Observation>[] npcPositions = so.getNPCPositions(avatarPosition);
//        return flatObservations(npcPositions);
//    }

    // game 0 immovables: category = 4, itype = 2
    // game 1 immovables: category = 4,4, type = 0,3
    // game 2 immovables: category = 4,4, type = 0,2
    @GPIgnore
    // used by Node's getNeighbors method; GP has objectively better methods to use (getRealDistance, getHeuristicDistance)
    public Iterable<Observation> getImmovablePositions(

//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5"}) int category,
//            @AllowedValues(values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}) int itype) {
            @AllowedValues(values = {"4"}) int category,
            @AllowedValues(values = {"3", "4"}) int itype) {
        Vector2d avatarPosition = so.getAvatarPosition();
        ArrayList<Observation>[] immovablePositions = so.getImmovablePositions(avatarPosition);
        List<Observation> positions = flatObservations(immovablePositions);
        List<Observation> filteredPositions = new ArrayList<>(positions.size());

        for (Observation position : positions) {
//            if (position.itype == itype)
            if (position.itype != 0)
                filteredPositions.add(position);
        }

        return filteredPositions;
    }

    //
    public Iterable<Double> getImmovableRealDistance(
//            @AllowedValues(values = {"4"}) int category,
//                                                     @AllowedValues(values = {"3", "4"}) int itype
    ) {
        ArrayList<Observation>[] immovablePositions = so.getImmovablePositions();
        List<Observation> positions = flatObservations(immovablePositions);
        List<Observation> filteredPositions = new ArrayList<>(positions.size());

        for (Observation position : positions) {
//            if (position.itype == itype)
            if (position.itype != 0)
                filteredPositions.add(position);
        }

        return getAStarDistances(filteredPositions, so.getAvatarPosition());
    }

    public Double getImmovableCount() {
        ArrayList<Observation>[] immovablePositions = so.getImmovablePositions();
        List<Observation> positions = flatObservations(immovablePositions);
        double count = 0;

        for (Observation position : positions) {
            if (position.itype != 0)
                count++;
        }

        return count;
    }


    public Iterable<Double> getPortalRealDistance() {
        ArrayList<Observation>[] portalsPositions = so.getPortalsPositions();
        return getAStarDistances(flatObservations(portalsPositions), so.getAvatarPosition());
    }

    public Iterable<Double> getMovableRealDistance() {
        ArrayList<Observation>[] movablePositions = so.getMovablePositions();
        return getAStarDistances(flatObservations(movablePositions), so.getAvatarPosition());
    }

    //    @GPIgnore
    public Iterable<Double> getMovableDistanceFromImmovable(@AllowedValues(values = {"3", "1", "2"}) int immovableIndex) {
        List<Observation> immovables = flatObservations(so.getImmovablePositions());
        List<Observation> movables = flatObservations(so.getMovablePositions());
        ArrayList<Double> distances = new ArrayList<>();

//        for (Observation portal : portals) {
        int i, encounteredPortals;
        for (i = 0, encounteredPortals = 0; encounteredPortals < immovableIndex && i < immovables.size(); i++) {
            if (immovables.get(i).itype != 0)
                encounteredPortals++;
        }
        Observation portal = immovables.get(i - 1);
//        if (portal.itype != 0) {
        List<Double> aStarDistances = getAStarDistances(movables, portal.position);
        distances.addAll(aStarDistances);
//        }
//        }
        return distances;
    }

    public Iterable<Double> getResourcesRealDistance() {
        List<Observation> resourcesPositions = flatObservations(so.getResourcesPositions());

        return getAStarDistances(resourcesPositions, so.getAvatarPosition());
    }

    public Iterable<Double> getNPCRealDistance() {
        List<Observation> npcPositions = flatObservations(so.getNPCPositions());

        return getAStarDistances(npcPositions, so.getAvatarPosition());
    }

    public Double getBlockedImmovablesCount() {
        Iterable<Double> movablesBlockedSidesCount = getMovablesBlockedSidesCount();
        int blocked = 0;
        for (Double blockedSizes : movablesBlockedSidesCount) {
            if (blockedSizes > 1)
                blocked++;
        }
        return Double.valueOf(blocked);
    }

    @GPIgnore
    public Iterable<Double> getMovablesBlockedSidesCount() {
        List<Observation> movables = flatObservations(so.getMovablePositions());
        List<Double> counts = new ArrayList<>();

        for (Observation movable : movables) {
            Position position = new Position(movable, so);
            counts.add((double) (4 - position.getNeighbors().size()));
        }

        return counts;
    }


    @GPIgnore
    protected List<Observation> flatObservations(List<Observation>[] observationsList) {
        List<Observation> result = new ArrayList<>();

        if (observationsList != null) {
            for (List<Observation> observations : observationsList) {
                for (Observation observation : observations) {
//                if (observation.itype != 0)
                    result.add(observation);
                }
            }
        }

        return result;
    }


    @GPIgnore
    protected List<Double> getAStarDistances(List<Observation> observationsList, Vector2d reference) {
        List<Double> result = new ArrayList<>();

        if (observationsList != null && !observationsList.isEmpty()) {

            for (Observation observation : observationsList) {
                double distance = getAStarLength(reference, observation);
                result.add(distance);
            }
        } else {
            result.add(0.1);
        }

        return result;
    }

    @GPIgnore
    protected List<Double> getHeuristicDistances(List<Observation>[] observationsList) {
        List<Double> result = new ArrayList<>();
        int blockSize = so.getBlockSize();

        if (observationsList != null) {
            for (List<Observation> observations : observationsList) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    double distance = observation.sqDist / blockSize + countBlockingWalls(observation);

                    result.add(distance);
                }
            }
        } else {
            result.add(0.0);
        }

        return result;
    }

    @GPIgnore
    protected int getAStarLength(Vector2d avatarPosition, Observation observation) {
        int blockSize = so.getBlockSize();
        Position start = new Position((int) avatarPosition.x / blockSize, (int) avatarPosition.y / blockSize, so);
        Position goal = new Position((int) observation.position.x / blockSize, (int) observation.position.y / blockSize, so);

        if (goal.equals(start))
            return 0;

        try {
            return aStar.aStarLength(new AStarPathRequest<Position>(start, goal));
        } catch (AStarException e) {  // hopefully there was no path
            return Integer.MAX_VALUE;
        }
    }

    @GPIgnore
    protected double countBlockingWalls(Observation dstObservation) {
        int walls = 0;
        ArrayList<Observation>[][] observationGrid = so.getObservationGrid();
        int currentX = (int) so.getAvatarPosition().x / so.getBlockSize(),
                currentY = (int) so.getAvatarPosition().y / so.getBlockSize();

        if (currentX < 0 || currentY < 0) {
            // this might happen due to the framework..
            return 0;
        }

        int dstX = (int) (dstObservation.position.x / so.getBlockSize());
        int dstY = (int) (dstObservation.position.y / so.getBlockSize());
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
