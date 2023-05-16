package fr.ufrst.m1info.projetcomp.m1comp2;

public class HeapEntry {

    private int adr;
    private int size;
    private int ref;

    public HeapEntry(int adr, int size, int ref) {
        this.adr = adr;
        this.size = size;
        this.ref = ref;
    }

    //Getters / setters

    public int getAdr() {
        return adr;
    }

    public void setAdr(int adr) {
        this.adr = adr;
    }

    public int getSize() {
        return size;
    }

    public void incRef() {
        this.ref++;
    }

    public void decRef() {
        this.ref--;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o) {
            return true;
        }
        // null check
        if (o == null) {
            return false;
        }
        // type check and cast
        if (getClass() != o.getClass()) {
            return false;
        }
        // field comparison
        HeapEntry he = (HeapEntry) o;
        return this.getAdr() == he.getAdr() && this.getSize() == he.getSize() && this.getRef() == he.getRef();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
