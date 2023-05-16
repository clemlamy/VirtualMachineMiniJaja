package fr.ufrst.m1info.projetcomp.m1comp2;

public class ArrayException extends Exception{
    public ArrayException(String message){
        super("Array error : "+message);
    }
}