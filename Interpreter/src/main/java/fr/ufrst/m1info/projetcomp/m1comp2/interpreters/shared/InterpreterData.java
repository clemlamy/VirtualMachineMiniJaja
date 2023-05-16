package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

public class InterpreterData {
    private Object[] args;

    public InterpreterData(Object... _args) {
        args = _args;
    }

    public Object[] getArgs() {
        return args;
    }
}

