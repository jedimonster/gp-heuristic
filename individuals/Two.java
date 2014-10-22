//package compiled_output;

import evolution_impl.GPProgram;

import java.util.List;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Two implements GPProgram<Integer> {

    public int getValue(int x) {
        return 5 * x + -87;
    }

    public static void main(String[] args) {
        System.out.println(new Two().getValue(Integer.parseInt(args[0])));
    }

    @Override
    public Integer run(List<Object> params) {
        return getValue((Integer)params.get(0));
    }
}
