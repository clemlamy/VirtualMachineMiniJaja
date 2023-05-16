package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.mjj;

import fr.ufrst.m1info.projetcomp.m1comp2.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InterpreterMjjVisitor implements MiniJajaVisitor {

    private Memory memory;
    private String output;
    private Debugger debugger;
    private int address;
    private Stack<CallStackElement> callStack;

    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    public void visitDebug(Node node){
        //address++;
        //System.out.println(node);
        if(debugger != null){
            try{
                //debugger.moveTo(address);
                debugger.visit(node);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    InterpreterMjjVisitor() {
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
    public Memory getMemory() {
        return memory;
    }

    public void setAddress(int adr) {
        address = adr;
    }

    @Override
    public String toString() {
        return output;
    }

    private String headerToString(ASTentetes headers,String name) throws InterpreterException, VisitorException {
        // int res(int x, int y)
        // => res@res:INTEGER,INTEGER,
        // => f@global
        // => f@main
        StringBuilder res = new StringBuilder(name+":");
        Type currType;

        while (!(headers.jjtGetChild(1) instanceof ASTenil)){
            currType = (Type) headers.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,null);
            res.append(currType);
            if(!(headers.jjtGetChild(1) instanceof ASTenil)){
                res.append(InterpreterMjj.PARAM_SEPARATOR);
            }
            headers = (ASTentetes) headers.jjtGetChild(1);
        }
        currType = (Type) headers.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,null);
        res.append(currType);

        return  res.toString();
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws InterpreterException {
        throw new InterpreterException(node + " : Acceptor not implemented in subclass.");
    }

    @Override
    public Object visit(ASTStart node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, data);
        visitDebug(node);
        return null;
    }

    @Override
    public Object visit(ASTclasse node, Object data) throws VisitorException {
        visitDebug(node);
        String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
        try {
            memory.declVar(ident , Type.toString(Type.OMEGA), Type.OMEGA);
            //dss
            node.jjtGetChild(1).jjtAccept(this,  InterpreterMode.DEFAULT);
            //mma
            node.jjtGetChild(2).jjtAccept(this, data);
            //retrait dss
            node.jjtGetChild(1).jjtAccept(this, InterpreterMode.REMOVE);
            //retrait ident
            visitDebug(node);
            memory.removeDecl(ident);
            if(debugger != null){
                debugger.endButton();
            }
        }catch (Exception e){
            throw new VisitorException(e.getMessage(), node.getLine(),node.getColumn());
        }
        return null;
    }

    @Override
    public Object visit(ASTident node, Object data) throws InterpreterException {
        try {
            if(memory.getValue(((String) node.jjtGetValue()))=="OMEGA") {
                throw new InterpreterException("Variable may not have been initialized.", node.getLine(),node.getColumn(), callStack);
            }
            return memory.getValue(((String) node.jjtGetValue()));
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
    }

    @Override
    public Object visit(ASTdecls node, Object data) throws VisitorException {
        if(data==InterpreterMode.DEFAULT) {
            node.jjtGetChild(0).jjtAccept(this, data);
            node.jjtGetChild(1).jjtAccept(this, data);
        }else{
            node.jjtGetChild(1).jjtAccept(this, data);
            node.jjtGetChild(0).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTvnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTcst node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(1)).jjtGetValue();
            if(data==InterpreterMode.DEFAULT) {
                Object value = node.jjtGetChild(2).jjtAccept(this, data);
                Type type = (Type) node.jjtGetChild(0).jjtAccept(this, data);

                memory.declCst(ident, value, type);
            }else{
                memory.removeDecl(ident);
            }
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTtableau node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(1)).jjtGetValue();
            if(data==InterpreterMode.DEFAULT) {
                int value = (int) node.jjtGetChild(2).jjtAccept(this, data);
                if(value<=0){
                    throw new InterpreterException("An array can't have a  negative or null size.", node.getLine(),node.getColumn(), callStack);
                }
                Type type = (Type) node.jjtGetChild(0).jjtAccept(this, data);

                memory.declTab(ident, value, type);
            }else{
                memory.removeDecl(ident);
            }
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTmethode node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(1)).jjtGetValue();
            if(data==InterpreterMode.DEFAULT) {
                Type type = (Type) node.jjtGetChild(0).jjtAccept(this, data);
                memory.declMethode(ident, node, type);
            }else{
                memory.removeDecl(ident);
            }
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTvar node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(1)).jjtGetValue();
            if(data==InterpreterMode.DEFAULT) {
                Object value = node.jjtGetChild(2).jjtAccept(this, data);
                Type type = (Type) node.jjtGetChild(0).jjtAccept(this, data);

                memory.declVar(ident, value, type);
            }else{
                memory.removeDecl(ident);
            }
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTvars node, Object data) throws VisitorException {
        if(data==InterpreterMode.DEFAULT) {
            node.jjtGetChild(0).jjtAccept(this, data);
            node.jjtGetChild(1).jjtAccept(this, data);
        }else{
            node.jjtGetChild(1).jjtAccept(this, data);
            node.jjtGetChild(0).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTvexp node, Object data) throws VisitorException {
        return node.jjtGetChild(0).jjtAccept(this,data);
    }

    @Override
    public Object visit(ASTomega node, Object data) {
        return "OMEGA";
    }

    @Override
    public Object visit(ASTmain node, Object data) throws VisitorException {
        visitDebug(node);
        //dvs
        node.jjtGetChild(0).jjtAccept(this, InterpreterMode.DEFAULT);
        //iss
        node.jjtGetChild(1).jjtAccept(this, data);
        //retrait dvs
        node.jjtGetChild(0).jjtAccept(this,InterpreterMode.REMOVE);
        return null;
    }

    @Override
    public Object visit(ASTentetes node, Object data) throws VisitorException {
        if(data==InterpreterMode.DEFAULT) {
            node.jjtGetChild(0).jjtAccept(this, data);
            node.jjtGetChild(1).jjtAccept(this, data);
        }else{
            node.jjtGetChild(1).jjtAccept(this, data);
            node.jjtGetChild(0).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTenil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTentete node, Object data) throws InterpreterException {
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(1)).jjtGetValue();
            if(data==InterpreterMode.REMOVE) {
                memory.removeDecl(ident);
            }
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTinstrs node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTinil node, Object data) {
        return null;
    }

    //TODO le retirer, peut etre
    @Override
    public Object visit(ASTinstr node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTretour node, Object data) throws VisitorException {
        visitDebug(node);
        Object value =node.jjtGetChild(0).jjtAccept(this, data);
        try {
            Type type = null;
            if (value instanceof Integer) type = Type.INTEGER;
            if (value instanceof Boolean) type = Type.BOOLEAN;
            if (value instanceof String) type = Type.OMEGA;
            memory.setType((String) memory.variableClass(), type);
            memory.assignVal((String) memory.variableClass(), value);
        }catch (Exception e){
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTecrire node, Object data) throws VisitorException {
        visitDebug(node);
        Object value =node.jjtGetChild(0).jjtAccept(this, data);
        try {
            output += value;
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        if(debugger != null){
            debugger.alertObservers((value+"").replace("\"", ""),true);
        }
        return null;
    }

    @Override
    public Object visit(ASTecrireln node, Object data) throws VisitorException {
        visitDebug(node);
        Object value =node.jjtGetChild(0).jjtAccept(this, data);
        try {
            output += value+"\n";
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        if(debugger != null){
            debugger.alertObservers((value+"\n").replace("\"", ""),true);
        }
        return null;
    }

    @Override
    public Object visit(ASTsi node, Object data) throws VisitorException {
        visitDebug(node);
        Object value =node.jjtGetChild(0).jjtAccept(this, data);
        try {
            if((boolean) value){
                node.jjtGetChild(1).jjtAccept(this, data);
            }else{
                node.jjtGetChild(2).jjtAccept(this, data);
            }
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTtantque node, Object data) throws VisitorException {
        visitDebug(node);
        Object value = node.jjtGetChild(0).jjtAccept(this, data);
        try {
            if((boolean) value){
                node.jjtGetChild(1).jjtAccept(this, data);
                node.jjtAccept(this,data);
            }
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTappelI node, Object data) throws InterpreterException {
        visitDebug(node);
        String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();

        Object lexp = node.jjtGetChild(1);

        try {
            Object entetes2 = memory.getParam(ident);
            //ExpParam
            while(entetes2.getClass()!= ASTenil.class){
                ASTentete entete = (ASTentete) ((ASTentetes) entetes2).jjtGetChild(0);
                String entete_ident = (String) ((ASTident)entete.jjtGetChild(1)).jjtGetValue();
                Type entete_type = (Type) entete.jjtGetChild(0).jjtAccept(this, InterpreterMode.DEFAULT);
                Object value = ((ASTlistexp) lexp).jjtGetChild(0).jjtAccept(this, InterpreterMode.DEFAULT);
                memory.declVar(entete_ident, value, entete_type);
                entetes2= ((ASTentetes)entetes2).jjtGetChild(1);
                lexp=((ASTlistexp) lexp).jjtGetChild(1);
            }
            //Declaration
            if(memory.getDecl(ident) instanceof ASTvars) {
                var dvs = (ASTvars) memory.getDecl(ident);
                dvs.jjtAccept(this, InterpreterMode.DEFAULT);
            }
            //Corps
            if(memory.getBody(ident) instanceof ASTinstrs) {
                ASTinstrs instrs = (ASTinstrs) memory.getBody(ident);
                instrs.jjtAccept(this, InterpreterMode.DEFAULT);
            }
            //Remove Declaration
            if(memory.getDecl(ident) instanceof ASTvars) {
                var dvs = (ASTvars) memory.getDecl(ident);
                dvs.jjtAccept(this, InterpreterMode.REMOVE);
            }
            //Remove Parameter
            if(memory.getParam(ident) instanceof ASTentetes) {
                ASTentetes entetes = (ASTentetes) memory.getParam(ident);
                entetes.jjtAccept(this, InterpreterMode.REMOVE);
            }

        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTtab node, Object data) throws VisitorException {
        String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
        int value = (int) node.jjtGetChild(1).jjtAccept(this, data);
        try {
            int array_size = memory.getArraySize(ident);
            if(value>=array_size || value<0){
                throw new InterpreterException("Unreachable statement in array : index out of bound.", node.getLine(),node.getColumn(), callStack);
            }
            return memory.getValueT(ident,value);
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
    }

    @Override
    public Object visit(ASTaffectation node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            var value = node.jjtGetChild(1).jjtAccept(this, data);
            if(node.jjtGetChild(0).getClass()== ASTtab.class){
                ASTtab tab = (ASTtab) node.jjtGetChild(0);
                String ident = (String) ((ASTident) tab.jjtGetChild(0)).jjtGetValue();
                int index = (int) tab.jjtGetChild(1).jjtAccept(this, data);
                int array_size = memory.getArraySize(ident);
                if(index>=array_size || index<0){
                    throw new InterpreterException("Unreachable statement in array : index out of bound.", node.getLine(),node.getColumn(), callStack);
                }
                memory.assignValT(ident,value,index);
            }else{
                String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
                memory.assignVal(ident,value);
            }
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTsomme node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            int value = (int) node.jjtGetChild(1).jjtAccept(this, data);
            if(node.jjtGetChild(0).getClass()== ASTtab.class){
                String ident = (String) ((ASTident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
                ASTtab tab = (ASTtab) node.jjtGetChild(0);
                int index = (int) tab.jjtGetChild(1).jjtAccept(this, data);
                int array_size = memory.getArraySize(ident);
                if(index>=array_size || index<0){
                    throw new InterpreterException("Unreachable statement in array : index out of bound.", node.getLine(),node.getColumn(), callStack);
                }
                int newValue = (int) memory.getValueT((ident),index) + value;
                memory.assignValT(ident,newValue,index);
            }else{
                String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
                int newValue = (int) memory.getValue(ident) + value;
                memory.assignVal(ident,newValue);
            }
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTincrement node, Object data) throws InterpreterException {
        visitDebug(node);
        try {
            if(node.jjtGetChild(0).getClass()== ASTtab.class){
                String ident = (String) ((ASTident) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
                ASTtab tab = (ASTtab) node.jjtGetChild(0);
                int index = (int) tab.jjtGetChild(1).jjtAccept(this, data);
                int array_size = memory.getArraySize(ident);
                if(index>=array_size || index<0){
                    throw new InterpreterException("Unreachable statement in array : index out of bound.", node.getLine(),node.getColumn(), callStack);
                }
                int newValue = (int) memory.getValueT((ident),index) + 1;
                memory.assignValT(ident,newValue,index);
            }else{
                String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
                int newValue = (int) memory.getValue(ident) + 1;
                memory.assignVal(ident,newValue);
            }
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
        return null;
    }

    @Override
    public Object visit(ASTlistexp node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTexnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTnon node, Object data) throws VisitorException {
        boolean value = (boolean) node.jjtGetChild(0).jjtAccept(this, data);
        return !value;
    }

    @Override
    public Object visit(ASTneg node, Object data) throws VisitorException {
        int value = (int) node.jjtGetChild(0).jjtAccept(this, data);
        return -value;
    }

    @Override
    public Object visit(ASTet node, Object data) throws VisitorException {
        boolean value1 = (boolean) node.jjtGetChild(0).jjtAccept(this, data);
        boolean value2 = (boolean) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 && value2;
    }

    @Override
    public Object visit(ASTou node, Object data) throws VisitorException {
        boolean value1 = (boolean) node.jjtGetChild(0).jjtAccept(this, data);
        boolean value2 = (boolean) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 || value2;
    }

    @Override
    public Object visit(ASTegal node, Object data) throws VisitorException {
        return node.jjtGetChild(0).jjtAccept(this, data) == node.jjtGetChild(1).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTinf node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 < value2;
    }

    @Override
    public Object visit(ASTsup node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 > value2;
    }

    @Override
    public Object visit(ASTplus node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 + value2;
    }

    @Override
    public Object visit(ASTmoins node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 - value2;
    }

    @Override
    public Object visit(ASTmult node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return value1 * value2;
    }

    @Override
    public Object visit(ASTdiv node, Object data) throws VisitorException {
        int value1 = (int) node.jjtGetChild(0).jjtAccept(this, data);
        int value2 = (int) node.jjtGetChild(1).jjtAccept(this, data);
        if(value2==0){
            throw new InterpreterException("You can't divide by 0.", node.getLine(),node.getColumn(), callStack);
        }
        return value1 / value2;
    }

    @Override
    public Object visit(ASTlongueur node, Object data) throws InterpreterException {
        try {
            String ident = (String) ((ASTident) node.jjtGetChild(0)).jjtGetValue();
            return memory.getArraySize(ident);
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
    }

    @Override
    public Object visit(ASTvrai node, Object data) {
        return true;
    }

    @Override
    public Object visit(ASTfaux node, Object data) {
        return false;
    }

    @Override
    public Object visit(ASTappelE node, Object data) throws InterpreterException {
        try {
            ASTappelI appeli = new ASTappelI(MiniJajaTreeConstants.JJTAPPELI);
            appeli.jjtAddChild(node.jjtGetChild(0),0);
            appeli.jjtAddChild(node.jjtGetChild(1),1);
            appeli.jjtAccept(this,data);
            return memory.getValue((String) memory.variableClass());
        } catch (Exception e) {
            throw new InterpreterException(e.getMessage(), node.getLine(),node.getColumn(), callStack);
        }
    }

    @Override
    public Object visit(ASTnbre node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTrien node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTentier node, Object data) {
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTbooleen node, Object data) {
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTchaine node, Object data) {
        return node.jjtGetValue();
    }
}
