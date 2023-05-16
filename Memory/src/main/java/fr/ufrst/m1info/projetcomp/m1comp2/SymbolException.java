package fr.ufrst.m1info.projetcomp.m1comp2;

public class SymbolException extends Exception{
    public SymbolException(String message){
        super("An error occurred during the operation : "+message);
    }
}
