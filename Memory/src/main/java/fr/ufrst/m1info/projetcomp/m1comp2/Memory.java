package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node;

public class Memory {
    private final SymbolTable symbolTable;
    private final Stack stack;

    private final Heap heap;

    public Memory(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
        stack = new Stack();
        try{
            heap = new Heap();
        } catch (HeapException e) {
            throw new RuntimeException(e); // TODO : maybe yeet
        }
    }

    /**
     * Getters
     */
    public Stack getStack(){
        return stack;
    }

    public SymbolTable getSymbolTable(){
        return symbolTable;
    }

    public Heap getHeap() {
        return heap;
    }

    /**
     * Methods
     */


    public void removePop() throws StackException, SymbolException, HeapException {
        var valTop = stack.dequeue();
        String id = valTop.getID();

        if(valTop.getNature()!=Nature.CST)
            symbolTable.delete(id);
        if(valTop.getNature().equals(Nature.TAB)){
            heap.decRef((Integer) valTop.getValue());
        }
    }
    public void declVar(String id, Object value, Type type) throws TypeException, SymbolException {
        Quad quad = new Quad(id, value, type, Nature.VAR);
        symbolTable.put(quad);
        stack.enqueue(quad);
    }

    public void identVal(String id, Type type, int s) throws StackException, SymbolException {
        Quad quad = stack.getFromTop(s);
        if(!quad.getID().equals(Type.toString(Type.OMEGA))){
            throw new SymbolException("Try to operate identVal on an other variable (or constant)");
        }
        quad.setID(id);
        quad.setType(type);
        quad.setNature(Nature.VAR);
        symbolTable.put(quad);
    }

    public void declCst(String id, Object value, Type nature) throws TypeException, SymbolException {
        Quad quad;
        if(value == null || value == "OMEGA"){
            quad = new Quad(id, null, nature, Nature.VCST);
        }else{
            System.out.println(value);
            quad = new Quad(id, value, nature, Nature.CST);
        }
        symbolTable.put(quad);
        stack.enqueue(quad);
    }

    public void declTab(String id, int size, Type type) throws SymbolException, TypeException, HeapException {
        int adr = heap.allocateMemory(size,type);
        Quad quad = new Quad(id, adr, type, Nature.TAB);
        symbolTable.put(quad);
        stack.enqueue(quad);
    }

    public void declMethode(String id, Object value, Type nature) throws TypeException, SymbolException {
        Quad quad = new Quad(id, value, nature, Nature.METH);
        symbolTable.put(quad);
        stack.enqueue(quad);
    }

    public void print(Object value) throws TypeException {
        Quad quad = new Quad(Type.toString(Type.OMEGA),value,Type.OMEGA,Nature.VAR);
        stack.enqueue(quad);
    }

    public void printLn(Object value) throws TypeException {
        Quad quad = new Quad(Type.toString(Type.OMEGA),value+"\n",Type.OMEGA, Nature.VAR);
        stack.enqueue(quad);
    }

    /*
    public void expParam(){
        // TODO : normalement c'est éléa qui fait, rappel lui si jamais (niveau de l'interpreteur)g
        if(){

        }
    }
    */

    public Quad removeDecl(String id) throws SymbolException, StackException, HeapException {
        Quad quad = symbolTable.get(id);
        stack.removeFirstOf(id);
        symbolTable.delete(id);
        if(quad.getNature().equals(Nature.TAB)){
            heap.decRef((Integer) quad.getValue());
        }
        return quad;
    }

    public Quad assignVal(String id, Object value) throws SymbolException, ConstantException, StackException, TypeException, HeapException {
        Quad quad = stack.getFirstOf(id);

        if(quad == null){
            throw new SymbolException("Trying to assign a value to an unknown symbol :"+id);
        }
        if(value == null){
            throw new TypeException("Cannot assign null value");
        }
        if(!value.getClass().getSimpleName().equalsIgnoreCase(quad.getType().toString().toLowerCase())){
            throw new TypeException(
                    String.format("Invalid affectation type : %s != %s",
                            value.getClass().getSimpleName(),
                            quad.getType().toString().toLowerCase())
            );
        }

        switch(quad.getNature()){
            case CST:
                throw new ConstantException(quad);
            case TAB:
                heap.decRef((Integer) quad.getValue());
                if(!(value instanceof Integer)){
                    throw new TypeException(String.format("%s is invalid for the type Integer",value));
                }
                quad.setValue(value);
                heap.incRef((Integer) value);
                break;
            case VCST:
                quad.setValue(value);
                quad.setNature(Nature.CST);
                break;
            case VAR:
                quad.setValue(value);
                break;
            case METH:
                return quad;
        }
        symbolTable.update(id,value);
        return quad;
    }

    public Quad assignValT(String id, Object value, int index) throws SymbolException, ArrayException, TypeException, HeapException {
        Quad quad = symbolTable.get(id);

        // Check if th Quad is an array
        if(!quad.getNature().equals(Nature.TAB)){
            throw new ArrayException("array operation on none array id ("+id+" is not an array)");
        }

        // Check if the value type matches the array type
        if(!value.getClass().getSimpleName().equalsIgnoreCase(quad.getType().toString())){
            throw new TypeException(String.format("%s is invalid for the type %s of the Array",value.getClass().getSimpleName().toLowerCase(),quad.getType()));
        }

        heap.setArrayValueAt((Integer) quad.getValue(), index, value);
        return quad;
    }

    public Object getValue(String id) throws SymbolException {
        Quad quad = symbolTable.get(id);
        if(quad.getValue() == null || quad.getValue().equals("OMEGA")){
            throw new SymbolException("Variable "+id+" may not have been initialized.");
        }
        return quad.getValue();
    }

    public Object getValueT(String id, int index) throws ArrayException, StackException, HeapException {
        Quad quad = stack.getFirstOf(id);
        // Check if th Quad is an array
        if(!quad.getNature().equals(Nature.TAB)){
            throw new ArrayException("array operation on none array id ("+id+" is not an array)");
        }

        return heap.getArrayValueAt((Integer) quad.getValue(),index);
    }

    public Object getNature(String id) throws SymbolException {
        Quad quad = symbolTable.get(id);
        return quad.getNature();
    }

    public Object getType(String id) throws SymbolException {
        Quad quad = symbolTable.get(id);
        return quad.getType();
    }
    public int getArraySize(String id) throws StackException, ArrayException, HeapException {
        Quad quad = stack.getFirstOf(id);
        // Check if th Quad is an array
        if(!quad.getNature().equals(Nature.TAB)){
            throw new ArrayException("array operation on none array id ("+id+" is not an array)");
        }
        return heap.getEntryLength((int)quad.getValue());
    }

    public Object getParam(String id) throws StackException {
        Quad quad = stack.getFirstOf(id);
        if(quad == null || !quad.getNature().equals(Nature.METH)){
            return null;
        }

        return ((Node) quad.getValue()).jjtGetChild(2);
    }

    public Object getDecl(String id) throws StackException {
        Quad quad = stack.getFirstOf(id);
        if(quad == null || !quad.getNature().equals(Nature.METH)){
            return null;
        }

        return ((Node) quad.getValue()).jjtGetChild(3);
    }

    public Object getBody(String id) throws StackException {
        Quad quad = stack.getFirstOf(id);
        if(quad == null || !quad.getNature().equals(Nature.METH)){
            return null;
        }

        return ((Node) quad.getValue()).jjtGetChild(4);
    }

    public Object variableClass() throws StackException {
        return stack.getBottom().getID();
    }

    public void setType(String id, Type type) throws SymbolException {
        Quad quad = symbolTable.get(id);
        quad.setType(type);
    }


    @Override
    public String toString(){
        return stack + "\n" + symbolTable + "\n";
    }
}
