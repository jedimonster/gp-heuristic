package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Three {

    public int getValue(int x) {
        return x + -1;
    }

    public static void main(String[] args) {
        System.out.println(new Three().getValue(Integer.parseInt(args[0])));
    }
}
