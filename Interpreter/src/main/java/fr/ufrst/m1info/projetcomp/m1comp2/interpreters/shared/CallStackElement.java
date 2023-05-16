package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

public class CallStackElement implements Localizable {
    private final String element;
    private final int line;
    private final int column;

    public CallStackElement(String element, int line, int column) {
        this.element = element;
        this.line = line;
        this.column = column;
    }

    public String getElement() {
        return element;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
