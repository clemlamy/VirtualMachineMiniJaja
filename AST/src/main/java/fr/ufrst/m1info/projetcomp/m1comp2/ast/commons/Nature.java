package fr.ufrst.m1info.projetcomp.m1comp2.ast.commons;

public enum Nature {
    VAR,
    CST,
    VCST,
    TAB,
    METH;

    public static Nature fromString(String s){
        switch (s){
            case "var":
                return VAR;
            case "cst":
                return CST;
            case "vcst":
                return VCST;
            case "tab":
                return TAB;
            case "meth":
                return METH;
            default:
                return VAR;
        }
    }
}