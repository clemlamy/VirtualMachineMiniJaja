package fr.ufrst.m1info.projetcomp.m1comp2;

public class ConstantException extends Exception{
    public ConstantException(Quad quad){
        super("Constant error : you are not allowed to modify an already defined constant ("+quad.getID()+" already defined)");
    }
}