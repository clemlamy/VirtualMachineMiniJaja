package fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj;

public class TypeCheckerException extends VisitorException{
    public TypeCheckerException(String s, int l, int c) {
        super("Ah... Little player : "+s,l,c);
    }
}
