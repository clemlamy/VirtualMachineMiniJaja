package fr.ufrst.m1info.projetcomp.m1comp2;

public class HeapException extends Exception {

    public HeapException(String message){
        super("An error occurred during the operation : "+message);
    }
}
