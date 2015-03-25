package evolution_impl.fitness.dummyagent;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.*;

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
                    double distance = observation.sqDist + countBlockingWalls(observation);

                    result.add(distance);
                }
            }
        }

        return result;
    }


    public Iterable<Double> getPortalsHeuristicDistance() {
        Vector2d avatarPosition = so.getAvatarPosition();
        List<Double> result = new ArrayList<>();
        final int blockSize = so.getBlockSize();

        ArrayList<Observation>[] portalsPositions = so.getPortalsPositions(avatarPosition);
        if (portalsPositions != null) {
            for (ArrayList<Observation> observations : portalsPositions) {
                for (Observation observation : observations) {
//                if (observation.category == category && observation.itype == itype)
                    double distance = aStar(new Position((int) avatarPosition.x / blockSize, (int) avatarPosition.y / blockSize), new Position((int) observation.position.x / blockSize, (int) observation.position.y / blockSize)).size();
//                    double distance=0;
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
                    double distance = observation.sqDist + countBlockingWalls(observation);

                    result.add(distance);
                }
            }
        }

        return result;
    }

    @GPIgnore
    private double countBlockingWalls(Observation dstObservation) {
        int walls = 0;
        ArrayList<Observation>[][] observationGrid = so.getObservationGrid();
        int currentX = (int) so.getAvatarPosition().x / so.getBlockSize(),
                currentY = (int) so.getAvatarPosition().y / so.getBlockSize();

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

    @GPIgnore
    private List<Position> aStar(Position start, Position goal) {
        final ArrayList<Position> path = new ArrayList();
        final HashSet<Position> closedSet = new HashSet<>();
        final HashMap<Position, Double> fScore = new HashMap<>();
        final SortedList<Position> openSet = new SortedList<Position>(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return fScore.get(o1).compareTo(fScore.get(o2));
            }
        });
        final HashMap<Position, Position> cameFrom = new HashMap<>();
        final HashMap<Position, Double> gScore = new HashMap<>();

        openSet.add(start);
        gScore.put(start, 0.0);
        fScore.put(start, gScore.get(start) + heuristicDistance(start, goal));

        while (!openSet.isEmpty()) {
            Position current = openSet.get(0); // sorted list so first is min.
            if (current.equals(goal))
                return reconstructPath(cameFrom, goal);

            openSet.remove(current);
            closedSet.add(current);

            for (Position neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore = gScore.get(current) + 1; // distance is always one.

                if (!openSet.contains(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) + heuristicDistance(neighbor, goal));

                    if (!openSet.contains(neighbor))
                        openSet.add(neighbor);
                }
            }
        }

        throw new RuntimeException("A* Failed");
    }

    @GPIgnore
    private List<Position> getNeighbors(Position current) {
        List<Observation> immovablePositions = (List<Observation>) getImmovablePositions(0, 0);

        int x = current.getX(), y = current.getY();
        List<Position> candidates = Arrays.asList(new Position[]{new Position(x - 1, y), new Position(x + 1, y), new Position(x, y - 1), new Position(x, y + 1)});
        ArrayList<Observation>[][] observationGrid = so.getObservationGrid();
        ArrayList<Position> neighbors = new ArrayList<>();
        boolean wall;

        for (Position candidate : candidates) {
            wall = false;
            if (candidate.getX() >= observationGrid.length || candidate.getX() < 0 || candidate.getY() >= observationGrid[0].length || candidate.getY() < 0)
                continue;

//            if (observationGrid[candidate.getX()][candidate.getY()].isEmpty()) // nothing there
            ArrayList<Observation> observations = observationGrid[candidate.getX()][candidate.getY()];
            for (int i = 0; i < observations.size() && !wall; i++) {
                Observation observation = observations.get(i);
                wall = immovablePositions.contains(observation);
            }
            if (!wall)
                neighbors.add(candidate);
        }

        return neighbors;
    }

    @GPIgnore
    private List<Position> reconstructPath(HashMap<Position, Position> cameFrom, Position current) {
        List<Position> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }

        return path;
    }

    @GPIgnore
    private Double heuristicDistance(Position start, Position goal) {
//        return (double) (Math.abs(start.getX() - goal.getX()) + Math.abs(start.getY() - goal.getY()));
        return 0.0;
    }

}
