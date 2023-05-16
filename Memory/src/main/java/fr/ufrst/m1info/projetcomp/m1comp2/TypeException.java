package fr.ufrst.m1info.projetcomp.m1comp2;

public class TypeException extends Exception{
    public TypeException(String message){
        super("Invalid type : "+message);
    }
}
