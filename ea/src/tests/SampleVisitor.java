package tests;

import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * Created By Itay Azaria
 * Date: 9/16/2014
 */
public class SampleVisitor extends VoidVisitorAdapter {
    @Override
    public void visit(WhileStmt n, Object arg) {
        super.visit(n, arg);
    }
}
