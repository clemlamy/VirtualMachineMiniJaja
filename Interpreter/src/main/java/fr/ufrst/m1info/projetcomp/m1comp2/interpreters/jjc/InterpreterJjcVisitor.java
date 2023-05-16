package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.jjc;

import fr.ufrst.m1info.projetcomp.m1comp2.Memory;
import fr.ufrst.m1info.projetcomp.m1comp2.Quad;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.CallStackElement;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.Debugger;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.InterpreterException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.VM;

import java.util.Stack;

public class InterpreterJjcVisitor implements JajaCodeVisitor {
    private Memory memory;
    private Debugger debugger;
    private String output;
    private int address;
    private Stack<CallStackElement> callStack;

    private int n;

    private ASTGoto lastGoto;

    InterpreterJjcVisitor() {
        callStack = new Stack<>();
        address = 1;
        output = "";
    }

    public void setMemory(Memory mem) {
        memory = mem;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int adr) {
        address = adr;
    }

    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    public void visitDebug(Node node){
        //System.out.println(node);
        if(debugger != null){
            try{
                debugger.visit(node);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    @Override
    public String toString() {
        return output;
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws InterpreterException {
        throw new InterpreterException(node + " : Acceptor not implemented in subclass.");
    }

    @Override
    public Object visit(ASTJajaCode node, Object data) throws VisitorException {
        if (address < 0)
            return null;

        int adrNodeToInterpret = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
        if (address == adrNodeToInterpret)
            return node.jjtGetChild(1).jjtAccept(this, data);

        return node.jjtGetChild(2).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTJCNil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTInit node, Object data) {
        visitDebug(node);
        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTSwap node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Quad first = memory.getStack().dequeue();
            Quad second = memory.getStack().dequeue();
            memory.getStack().enqueue(first);
            memory.getStack().enqueue(second);
        } catch(Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }
        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTNew node, Object data) throws VisitorException {
        visitDebug(node);
        String ident       = (String)  node.jjtGetChild(0).jjtAccept(this, data);
        Type type          = (Type)    node.jjtGetChild(1).jjtAccept(this, data);
        Nature natureValue = (Nature)  node.jjtGetChild(2).jjtAccept(this, data);
        var position       = (int)     node.jjtGetChild(3).jjtAccept(this, data);

        switch (natureValue) {
            case VAR:
                try {
                    //Quad value = memory.getStack().getFromTop(position);
                    //Quad value = memory.identVal(ident,type,position);
                    memory.identVal(ident, type, position);
                } catch (Exception e) {
                    throw new InterpreterException(e.getMessage(), address, 0, callStack);
                }
                break;

            case CST:
                try {
                    Quad value = memory.getStack().dequeue();
                    memory.declCst(ident, value.getValue(), type);
                } catch (Exception e) {
                    throw new InterpreterException(e.getMessage(), address, 0, callStack);
                }
                break;

            case METH:
                try {
                    Quad value = memory.getStack().dequeue();
                    memory.declMethode(ident, value.getValue(), type);
                } catch (Exception e) {
                    throw new InterpreterException(e.getMessage(), address, 0, callStack);
                }
                break;

            default:
                throw new InterpreterException(
                    "Invalid declaration.",
                    address, 0,
                    callStack
                );
        }
        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTNewArray node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        try {
            int size = (Integer) memory.getStack().dequeue().getValue();
            if(size<=0){
                throw new InterpreterException("An array can't have a  negative or null size.", address,0, callStack);
            }
            Type type = (Type) node.jjtGetChild(1).jjtAccept(this,data);
            memory.declTab(ident,size,type);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }
        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTInvoke node, Object data) throws VisitorException {
        visitDebug(node);

        if (callStack.size() > VM.MAX_RECURSIVE_CALL) {
            throw new InterpreterException("Maximum call stack size exceeded.", address, 0, callStack);
        }

        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        callStack.push(new CallStackElement(ident, 0, 0));

        try {
            int methodAddress = (int) memory.getValue(ident);

            // Save current address
            memory.getStack().enqueue(
                new Quad(
                    Type.toString(Type.OMEGA),
                    address + 1,
                    Type.INTEGER,
                    Nature.CST
                )
            );

            address = methodAddress;
            if(debugger != null){
                debugger.moveTo(address);
            }
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) throws InterpreterException {
        visitDebug(node);

        try {
            callStack.pop();
        } catch (Exception exception) {
            // Nothing to do
        }

        try {
            address = (int) memory.getStack().dequeue().getValue();
            if (debugger != null)
                debugger.moveTo(address);
        } catch (Exception e) {
            throw new InterpreterException(
                e.getMessage(),
                address, 0,
                callStack
            );
        }

        return null;
    }

    @Override
    public Object visit(ASTWrite node, Object data) throws InterpreterException {
        visitDebug(node);
        Quad top;
        try {
            top = memory.getStack().dequeue();//.getFromTop(0);
            output += top.getValue();
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }
        if(debugger != null){
            debugger.alertObservers((top.getValue()+"").replace("\"", ""),false);
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTWriteln node, Object data) throws InterpreterException {
        visitDebug(node);
        Quad top;
        try {
            top = memory.getStack().dequeue();//.getFromTop(0);
            output += top.getValue()+"\n";
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        if(debugger != null){
            debugger.alertObservers((top.getValue()+"\n").replace("\"", ""),false);
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTPush node, Object data) throws VisitorException {
        visitDebug(node);
        Node nodeValue = node.jjtGetChild(0);

        Object value = nodeValue.jjtAccept(this, data);
        Type type = null;
        if (value instanceof Integer) type = Type.INTEGER;
        if (value instanceof Boolean) type = Type.BOOLEAN;
        if (value instanceof String) type = Type.OMEGA;

        try {
            memory.getStack().enqueue(new Quad(
                Type.toString(Type.OMEGA),
                value,
                type,
                Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTPop node, Object data) throws VisitorException {
        visitDebug(node);
        try {
            memory.removePop();
        }
        catch (Exception e) {
            throw new InterpreterException(
                e.getMessage(),
                address, 0,
                callStack
            );
        }
        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTLoad node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        Object value;
        try {
            value = memory.getValue(ident);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        if (value == null) {
            throw new InterpreterException(
                    "Variable \"" + ident + "\" might not have been initialized.",
                    address, 0,
                    callStack
            );
        }

        try {
            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value,
                    (Type) memory.getType(ident),
                    (Nature) Nature.CST
                )
            );
        } catch (Exception exception) {
            throw new InterpreterException(
                    exception.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTALoad node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        Object value;
        try {
            int index = (int) memory.getStack().dequeue().getValue();
            int array_size = memory.getArraySize(ident);
            if(index>=array_size || index<0){
                throw new InterpreterException("Unreachable statement in array : index out of bound.", address,0, callStack);
            }
            value = memory.getValueT(ident,(int)index);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        try {
            memory.getStack().enqueue(new Quad(
                            Type.toString(Type.OMEGA),
                            value,
                            (Type) memory.getType(ident),
                            Nature.CST
                    )
            );
        } catch (Exception exception) {
            throw new InterpreterException(
                    exception.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTStore node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        try {
            Object valueTopStack = memory.getStack().dequeue().getValue();

            memory.assignVal(ident, valueTopStack);
        } catch (Exception exception) {
            throw new InterpreterException(
                exception.getMessage(),
                address, 0,
                callStack
            );
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTAStore node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        try {
            // push index
            // push value
            // astore
            Object value = memory.getStack().dequeue().getValue();
            int index = (int) memory.getStack().dequeue().getValue();
            int array_size = memory.getArraySize(ident);
            if(index>=array_size || index<0){
                throw new InterpreterException("Unreachable statement in array : index out of bound.", address,0, callStack);
            }

            memory.assignValT(ident, value, (int)index);
        } catch (Exception exception) {
            throw new InterpreterException(
                    exception.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTIf node, Object data) throws VisitorException {
        visitDebug(node);
        int adr = (Integer) node.jjtGetChild(0).jjtAccept(this, data);

        try {
            boolean condition = (boolean) memory.getStack().dequeue().getValue();

            if (condition) {
                address = adr;
                if(debugger != null) {
                    debugger.moveTo(adr);
                }
            }else {
                address += 1;
            }
        } catch (Exception exception) {
            throw new InterpreterException(
                exception.getMessage(),
                address, 0,
                callStack
            );
        }

        return null;
    }

    @Override
    public Object visit(ASTGoto node, Object data) throws VisitorException {
        visitDebug(node);

        if (lastGoto == node) {
            if (n++ > VM.MAX_LOOP) {
                throw new InterpreterException("Infinite loop detected.", address, 0, callStack);
            }
        } else {
            n = 0;
            lastGoto = node;
        }

        address = (Integer) node.jjtGetChild(0).jjtAccept(this, data);
        if(debugger != null){
            debugger.moveTo(address);
        }
        return null;
    }

    @Override
    public Object visit(ASTInc node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        Integer value;
        Integer current;

        try {
            // Get value on top of stack
            value = (Integer) memory.getStack().dequeue().getValue();
            current = (Integer) memory.getValue(ident);
        } catch (Exception exception) {
            throw new InterpreterException(exception.getMessage(), address, 0, callStack);
        }

        if (current == null) {
            // Check that the variable has been initialized
            throw new InterpreterException("Variable \"" + ident + "\" might not have been initialized.", address, 0, callStack);
        }

        try {
            memory.assignVal(ident, current + value);
        } catch (Exception exception) {
            throw new InterpreterException(exception.getMessage(), address, 0, callStack);
        }

        address += 1;

        return null;
    }

    @Override
    public Object visit(ASTAInc node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) node.jjtGetChild(0).jjtAccept(this, data);

        Integer value;
        Integer current;
        Integer index;

        try {
            // Get value on top of stack
            value = (Integer) memory.getStack().dequeue().getValue();
            index = (Integer) memory.getStack().dequeue().getValue();
            int array_size = memory.getArraySize(ident);
            if(index>=array_size || index<0){
                throw new InterpreterException("Unreachable statement in array : index out of bound.", address,0, callStack);
            }
            current = (Integer) memory.getValueT(ident,index);
        } catch (Exception exception) {
            throw new InterpreterException(exception.getMessage(), address, 0, callStack);
        }

        try {
            memory.assignValT(ident, current + value, index);
        } catch (Exception exception) {
            throw new InterpreterException(exception.getMessage(), address, 0, callStack);
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTNop node, Object data) {
        visitDebug(node);
        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTJCStop node, Object data) {
        visitDebug(node);
        address = -1;
        return null;
    }

    @Override
    public Object visit(ASTJCIdent node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTJCType node, Object data) {
        return node.getType();
    }

    @Override
    public Object visit(ASTJCSorte node, Object data) {
        return node.getNature();
    }

    @Override
    public Object visit(ASTJCVrai node, Object data) {
        return true;
    }

    @Override
    public Object visit(ASTJCFaux node, Object data) {
        return false;
    }

    @Override
    public Object visit(ASTNeg node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Quad top = memory.getStack().dequeue();
            top.setValue((Integer) top.getValue() * -1);

            memory.getStack().enqueue(top);
        } catch (Exception e) {
            throw new InterpreterException(
                e.getMessage(),
                address, 0,
                callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTNot node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Quad top = memory.getStack().dequeue();
            top.setValue(!(Boolean) top.getValue());

            memory.getStack().enqueue(top);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTAdd node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value2 = (Integer) memory.getStack().dequeue().getValue();

            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            Quad res = new Quad(
                    Type.toString(Type.OMEGA),
                    value1 + value2,
                    Type.INTEGER,
                    Nature.CST
            );
            memory.getStack().enqueue(res);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTSub node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value2 = (Integer) memory.getStack().dequeue().getValue();

            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            Quad res = new Quad(
                    Type.toString(Type.OMEGA),
                    value1 - value2,
                    Type.INTEGER,
                    Nature.CST
            );
            memory.getStack().enqueue(res);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTMul node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value2 = (Integer) memory.getStack().dequeue().getValue();

            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            Quad res = new Quad(
                    Type.toString(Type.OMEGA),
                    value1 * value2,
                    Type.INTEGER,
                    Nature.CST
            );
            memory.getStack().enqueue(res);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTDiv node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value2 = (Integer) memory.getStack().dequeue().getValue();

            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            if(value2==0){
                throw new InterpreterException("You can't divide by 0 !!! Your mother didn't tell you that ?!", address,0, callStack);
            }

            Quad res = new Quad(
                    Type.toString(Type.OMEGA),
                    value1 / value2,
                    Type.INTEGER,
                    Nature.CST
            );
            memory.getStack().enqueue(res);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTCmp node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Object value1 = memory.getStack().dequeue().getValue();

            Object value2 = memory.getStack().dequeue().getValue();

            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value1.equals(value2),
                    Type.BOOLEAN,
                    Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTSup node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            Integer value2 = (Integer) memory.getStack().dequeue().getValue();
            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value2 > value1,
                    Type.BOOLEAN,
                    Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTInf node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Integer value1 = (Integer) memory.getStack().dequeue().getValue();

            Integer value2 = (Integer) memory.getStack().dequeue().getValue();

            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value2 < value1,
                    Type.BOOLEAN,
                    Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTOr node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Boolean value1 = (Boolean) memory.getStack().dequeue().getValue();

            Boolean value2 = (Boolean) memory.getStack().dequeue().getValue();

            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value1 || value2,
                    Type.BOOLEAN,
                    Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTAnd node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            Boolean value1 = (Boolean) memory.getStack().dequeue().getValue();

            Boolean value2 = (Boolean) memory.getStack().dequeue().getValue();

            memory.getStack().enqueue(new Quad(
                    Type.toString(Type.OMEGA),
                    value1 && value2,
                    Type.BOOLEAN,
                    Nature.CST)
            );
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }

        address += 1;
        return null;
    }

    @Override
    public Object visit(ASTJCNbre node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTJCString node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTLength node, Object data) throws VisitorException {
        try {
            var ident = (String) ((ASTJCIdent) node.jjtGetChild(0)).jjtGetValue();

            address += 1;

            return memory.getArraySize(ident);
        } catch (Exception e) {
            throw new InterpreterException(
                    e.getMessage(),
                    address, 0,
                    callStack
            );
        }
    }
}
