package fr.ufrst.m1info.projetcomp.m1comp2.gui;

public class IOResult<T> {

    private T data;
    private boolean ok;

    public IOResult(boolean ok, T data) {
        this.ok = ok;
        this.data = data;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean hasData() {
        return data != null;
    }

    public T getData() {
        return data;
    }
}
