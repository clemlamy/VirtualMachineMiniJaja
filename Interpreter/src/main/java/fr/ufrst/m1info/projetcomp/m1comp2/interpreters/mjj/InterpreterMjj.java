package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.mjj;

import fr.ufrst.m1info.projetcomp.m1comp2.Memory;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.Debugger;

import java.util.List;

public class InterpreterMjj {
    private final Node root;


    private final InterpreterMjjVisitor interpreterMjjVisitor;
    public static final String PARAM_SEPARATOR = ",";

    private final Memory memory;
    public InterpreterMjj(Node node, Memory memory) {
        root = node;
        interpreterMjjVisitor = new InterpreterMjjVisitor();
        interpreterMjjVisitor.setMemory(memory);
        this.memory = memory;
    }

    public String interpret() throws VisitorException {
        root.jjtAccept(interpreterMjjVisitor, null);
        return interpreterMjjVisitor.toString();
    }

    public void setDebugger(Debugger debugger){
        interpreterMjjVisitor.setDebugger(debugger);
    }

    public Memory getMemory() {
        return memory;
    }

}
