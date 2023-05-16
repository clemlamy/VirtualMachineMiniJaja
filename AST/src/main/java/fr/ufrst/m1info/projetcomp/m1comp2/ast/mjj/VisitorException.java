package fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj;

public class VisitorException extends Exception {
    private int l,c;

    public VisitorException(String s, int l, int c) {
        super(s);
        this.l = l;
        this.c = c;
    }

    public int getC() {
        return c;
    }

    public int getL() {
        return l;
    }
}
