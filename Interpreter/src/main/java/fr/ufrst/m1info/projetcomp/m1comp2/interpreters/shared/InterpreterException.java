package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

import java.util.Stack;

public class InterpreterException extends LocalizableRuntimeException {
    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(String message, int line, int column) {
        super(message, line, column);
    }

    public InterpreterException(String message, int line, int column, Stack<CallStackElement> callStack) {
        super(message, line, column, callStack);
    }
}
