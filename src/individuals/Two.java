package individuals;

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
public class Two {

    public int getValue(int x) {
        return x + 30;
    }

    public static void main(String[] args) {
        System.out.println(new Two().getValue(Integer.parseInt(args[0])));
    }
}
