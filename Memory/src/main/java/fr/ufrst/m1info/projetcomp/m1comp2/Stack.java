package fr.ufrst.m1info.projetcomp.m1comp2;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private int top;
    private final List<Quad> values;

    public Stack(){
        top = -1;
        values = new ArrayList<>();
    }

    // Getters
    public List<Quad> getValues(){
        return values;
    }

    public Quad getFromTop(int index) throws StackException {
        if(isEmpty()){
            throw new StackException("Trying to perform identVal on an empty stack");
        }
        try{
            return values.get(top - index);
        }catch(Exception e){
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean isEmpty(){
        return top < 0;
    }

    public Quad peek() throws StackException {
        if(isEmpty()){
            throw new StackException("Trying to get the 1st element of an empty stack");
        }
        return values.get(top);
    }

    public Quad enqueue(Quad q){
        values.add(q);
        ++top;
        return q;
    }

    public Quad dequeue() throws StackException {
        if(isEmpty()) {
            throw new StackException("Trying to remove element from an empty stack");
        }
        Quad toRemove = values.remove(top);
        --top;
        return toRemove;
    }

    public void swap() throws StackException {
        if(top < 1) {
            throw new StackException("Trying to swap elements from stack without 2 elements");
        }
        Quad tmp1 = values.get(top);
        Quad tmp2 = values.get(top-1);
        values.set(top-1,tmp1);
        values.set(top,tmp2);
    }

    public Quad getFirstOf(String id) throws StackException {
        if(isEmpty()){
            throw new StackException("Trying to get an element in an empty stack");
        }
        for(int i = 0 ; i <= top ; ++i){
            Quad quad = getFromTop(i);
            if(quad.getID().equals(id)){
                return quad;
            }
        }
        throw new StackException("Trying to get an unknown element in an empty stack: " +id);
    }

    public Quad getBottom() throws StackException {
        if(isEmpty()){
            throw new StackException("Trying to get the bottom element of an empty stack");
        }
        return values.get(top - (values.size()-1));
    }

    public Quad removeFirstOf(String id) throws StackException {
        Quad deleted = null;
        for(int i = 0 ; i <= top ; ++i){
            Quad quad = getFromTop(i);
            if(quad.getID().equals(id)){
                deleted = values.remove(top - i);
                top -= 1;
                break;
            }
        }
        return deleted;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("Stack:\n");
        for(Quad quad : values){
            s.append("\t").append(quad).append("\n");
        }
        return s.toString();
    }
}
