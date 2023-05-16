package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import java.util.ArrayList;

public class OutputConsole {
    private ArrayList<Error> errors;
    private ArrayList<Warning> warnings;
    boolean succeeded;
    boolean perfect;
    String executionMode;

    /***********************************
     *          GETTERS
     **********************************/

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    /***********************************
     *          SETTERS
     **********************************/

    public void setExecutionMode(String exec) {
        executionMode = exec;
    }

    /***********************************
     *          METHODS
     **********************************/

    OutputConsole(String exec) {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        succeeded = true;
        perfect = true;
        executionMode = exec;
    }

    public void addError(String msg, int line, int col) {
        Error err = new Error(msg, line, col);
        errors.add(err);
        succeeded = false;
        perfect = false;
    }

    public void addWarning(String msg, int line, int col) {
        Warning war = new Warning(msg, line, col);
        warnings.add(war);
        perfect = false;
    }

    public String showErrors() {
        StringBuilder allErrors = new StringBuilder();
        for (Error error : errors) {
            allErrors.append(error.toString()).append("\n");
        }
        return allErrors.toString();
    }

    public String showWarnings() {
        StringBuilder allWarnings = new StringBuilder();
        for (Warning warning : warnings) {
            allWarnings.append(warning.toString()).append("\n");
        }
        return allWarnings.toString();
    }

    public String showAllLogs() {
        String countWaringErrors = "(" + warnings.size() + " warning" + (warnings.size() == 1 ? "" : "s") + ", " + errors.size() + " error" + (errors.size() == 1 ? "" : "s") + ")";

        if (! succeeded) {
            return showErrors() + showWarnings() + "\nThe " + executionMode.toLowerCase() + " failed " + countWaringErrors + ".";
        }
        if (! perfect) {
            return showWarnings() + "\n" + executionMode + " succeed, but warning(s) exists " + countWaringErrors + ".";
        }
        return executionMode + " succeed ! " + countWaringErrors;
    }

    public void removeAllLogs() {
        errors.clear();
        warnings.clear();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    /***********************************
     *      INTERNAL CLASSES
     **********************************/

    public static class Log {
        protected String message;
        protected int line;
        protected int col;

        Log(String msg, int l, int c) {
            this.message = msg;
            this.line = l;
            this.col = c;
        }

        String getMessage() {
            return message;
        }

        int getLine() {
            return line;
        }

        int getCol() {
            return col;
        }
    }

    public static class Error extends Log {
        Error(String msg, int l, int c) {
            super(msg, l, c);
        }

        public String toString() {
            return "[" + this.line + ":" + this.col + "] Error : " + message +";";
        }
    }

    public static class Warning extends Log {
        Warning(String msg, int l, int c) {
            super(msg, l, c);
        }

        public String toString() {
            return "Warning [" + this.line + ":" + this.col + "] Warning : " + message +";";
        }
    }
}
