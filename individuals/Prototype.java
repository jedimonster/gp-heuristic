//package compiled_output;

import core.game.Observation;
import evolution_impl.GPProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Ind implements GPProgram {
    //    @Override
//    public Double run(List<Object> params) {
//        Object t = params;
//        return null;
//    }

    /**
     * Used by the GP to extract Observations of one type from the array of available observations.
     * This is useful because we want to create ADFs that work on one consistent type of observations
     * (they might be applicable to others, the GP can attempt to change type, but they're definitely useless if they work on a random type.)
     *
     * @param all  array of Lists of observations as returned by the StateObservation
     * @param type type to filter by (int whose range depends on the category, which is in turned defined when requesting @all from StateObservation.
     * @return Observations of type @type or empty List if none.
     */
    public List<Observation> extractObservationsOfType(List<Observation>[] all, int type) {
        List<Observation> result = new ArrayList<>();

        for (List<Observation> observations : all) {
            if (observations.size() > 0 && observations.get(0).itype == type)
                result.addAll(observations);
        }

        return result;
    }
}