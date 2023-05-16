package fr.ufrst.m1info.projetcomp.m1comp2.ast.commons;

public enum Type {
    INTEGER,
    BOOLEAN,
    VOID,
    OMEGA;

    public static String toString(Type type){
        switch (type){
            case INTEGER:
            case BOOLEAN:
            case VOID:
                return type.toString();
            default:
                return "?";
        }
    }
}

