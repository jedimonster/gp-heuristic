import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import tests.SampleVisitor;

import java.io.File;
import java.io.IOException;

/**
 * Created By Itay Azaria
 * Date: 7/8/2014
 */
public class Main {
    public static void main(String[] args) {
        try {
            File f = new File("codebase/sampleRandom/Agent.java");
            final CompilationUnit cu = JavaParser.parse(f);
            new SampleVisitor().visit(cu, null);
            System.out.println(cu);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
