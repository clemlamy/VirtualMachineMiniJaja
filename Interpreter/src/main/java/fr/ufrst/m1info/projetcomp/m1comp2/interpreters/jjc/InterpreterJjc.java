package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.jjc;

import fr.ufrst.m1info.projetcomp.m1comp2.Memory;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.Debugger;

import java.util.List;

public class InterpreterJjc{
    private final List<Node> instructions;

    private final InterpreterJjcVisitor interpreterJjcVisitor;

    private final Memory memory;

    public InterpreterJjc(List<Node> instrs, Memory memory) {
        instructions = instrs;
        interpreterJjcVisitor = new InterpreterJjcVisitor();
        interpreterJjcVisitor.setMemory(memory);
        this.memory = memory;
    }

    public Memory getMemory() {
        return memory;
    }

    public String interpret() throws VisitorException {
        int currentInstr = 1;
        while (currentInstr < instructions.size()) {
            //System.out.println("curr adr = "+currentInstr);
            instructions.get(currentInstr - 1).jjtAccept(interpreterJjcVisitor, null);
            currentInstr = interpreterJjcVisitor.getAddress();
        }
        instructions.get(instructions.size() - 1).jjtAccept(interpreterJjcVisitor, null);
        return interpreterJjcVisitor.toString();
    }

    public void setDebugger(Debugger debugger){
        interpreterJjcVisitor.setDebugger(debugger);
    }
}
