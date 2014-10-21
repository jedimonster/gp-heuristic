package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class One {

    public int getValue(int x) {
        return 5 * x + -87;
    }

    public static void main(String[] args) {
        System.out.println(new One().getValue(Integer.parseInt(args[0])));
    }
}
