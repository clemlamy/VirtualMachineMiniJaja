package fr.ufrst.m1info.projetcomp.m1comp2;

public class StackException extends Exception{
    public StackException(String message){
        super("Bad operation on stack : "+message);
    }
}
