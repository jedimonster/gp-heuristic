package evolution_impl.fitness.dummyagent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Itay on 27-Dec-15.
 */
public
@Retention(RetentionPolicy.RUNTIME)
@interface ValuesFromList {
    String listName();
}
