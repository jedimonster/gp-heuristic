package evolution_impl;

import java.util.List;

/**
 * Created by itayaza on 21/10/2014.
 */
public interface GPProgram<T> {
    public T run(List<Object> params);
}
