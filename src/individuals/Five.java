package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Five {

    public int getValue(int x) {
        return 9 * x + 25;
    }

    public static void main(String[] args) {
        System.out.println(new Five().getValue(Integer.parseInt(args[0])));
    }
}
