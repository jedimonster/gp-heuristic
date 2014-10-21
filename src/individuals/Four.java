package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Four {

    public int getValue(int x) {
        return -2 * x + 32;
    }

    public static void main(String[] args) {
        System.out.println(new Four().getValue(Integer.parseInt(args[0])));
    }
}
