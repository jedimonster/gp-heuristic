package evolution_impl.fitness.dummyagent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by itayaza on 28/12/2014.
 */
public
@Retention(RetentionPolicy.RUNTIME)
@interface AllowedValues {
    public String[] values();
}
