package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Six {

    public int getValue(int x) {
        return 0 * x + 27;
    }

    public static void main(String[] args) {
        System.out.println(new Six().getValue(Integer.parseInt(args[0])));
    }
}
