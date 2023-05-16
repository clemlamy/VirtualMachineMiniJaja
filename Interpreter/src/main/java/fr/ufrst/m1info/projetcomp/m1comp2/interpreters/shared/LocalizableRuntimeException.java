package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

import java.util.Stack;

public abstract class LocalizableRuntimeException
        extends VisitorException
        implements Localizable
{
    private final int line, column;

    private Stack<CallStackElement> callStack;

    public LocalizableRuntimeException(String message) {
        this(message, 0, 0, null);
    }

    public LocalizableRuntimeException(String message, int line, int column) {
        this(message, line, column, null);
    }

    public LocalizableRuntimeException(String message, int line, int column, Stack<CallStackElement> callStack) {
        super(message,line,column);

        this.line = line;
        this.column = column;
        this.callStack = callStack;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    public Stack<CallStackElement> getCallStack() {
        return callStack;
    }

}
