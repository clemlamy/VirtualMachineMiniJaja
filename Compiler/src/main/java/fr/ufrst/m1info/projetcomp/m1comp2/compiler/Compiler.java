package fr.ufrst.m1info.projetcomp.m1comp2.compiler;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

import java.util.ArrayList;

public class Compiler {
    final private Node root;

    final private CompilerVisitor compilerVisitor;

    public Compiler(Node node) {
        root = node;
        compilerVisitor = new CompilerVisitor();
    }

    public ArrayList<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> compile() throws VisitorException {
        compilerVisitor.clearListInstr();
        root.jjtAccept(compilerVisitor, new CompilerData(1, CompilerMode.DEFAULT));
        return compilerVisitor.getListInstr();
    }
}
