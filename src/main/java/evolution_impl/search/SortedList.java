package evolution_impl.search;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Itay on 25/03/2015.
 */
public class SortedList<T> extends ArrayList<T> {
    private Comparator<T> comparator;

    public SortedList(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean add(T t) {
        int i;
        for (i = 0; i < size() && comparator.compare(t, get(i)) > 0; i++)
            ;
        super.add(i, t);
        return true;
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("can't force inserting at specific in sorted list");
    }
}
