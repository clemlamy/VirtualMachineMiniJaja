package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SymbolTable {

    private static final int SIZE = 64;

    private final List<List<Quad>> buckets;

    public SymbolTable() {
        buckets = new ArrayList<>();
        for(int i = 0; i<SIZE; i++) {
            buckets.add(new LinkedList<>());
        }
    }

    //  Getters / Setters
    public List<List<Quad>> getBuckets() {
        return buckets;
    }


    public void put(Quad quad) throws SymbolException {
        int hashCode = hash(quad.getID());

        List<Quad> bucket = buckets.get(hashCode);
        if(quad.getID().contains("@main") || quad.getID().contains("@global") || quad.getNature().equals(Nature.METH)){
            for(Quad q : bucket) {
                if(q.equals(quad) || q.getID().equals(quad.getID())) {
                    throw new SymbolException("Same entry has already been found in the symbol table");
                }
            }
        }
        bucket.add(quad);
    }

    public Quad delete(String id) throws SymbolException {
        int toDeleted = -1;
        int hashCode = hash(id);
        int bucketSize = this.buckets.get(hashCode).size();
        //Case already one element inside the bucket
        for(int i = 0; i<bucketSize; i++) {
            String currID = this.buckets.get(hashCode).get(i).getID();
            if(currID.equals(id)) {
                toDeleted = i;//this.buckets.get(hashCode).remove(i);
                //break;
            }
        }
        if(toDeleted == -1){
            throw new SymbolException("Trying to delete an entry that is not in the symbol table");
        }
        return buckets.get(hashCode).remove(toDeleted);

    }

    public Quad get(String id) throws SymbolException {
        int hashCode = hash(id);
        List<Quad> tmp = this.buckets.get(hashCode);
        Quad needle = null;
        //No exception encountered --> we continue the process
        for (Quad element : tmp) {
            if (element.getID().equals(id)) {
                needle = element;
            }
        }

        if(needle != null){
            return needle;
        }
        throw new SymbolException("symbol not found in the table");
    }

    public void update(String id,Object value) throws SymbolException, TypeException {

        Quad currQuad = get(id);

        if(currQuad.getValue().getClass() != value.getClass()) {
            throw new TypeException("Value type and entry one are not the same");
        }

        currQuad.setValue(value);
    }

    public static int hash(String id){
        // DJB1 algorithm
        int hashcode = 5381;
        for(char c : id.toCharArray()){
            //hashcode = c + ((hashcode << 5) - hashcode);
            hashcode *= 33;
            hashcode += c;
        }
        return Math.abs(hashcode)%SIZE;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Symbol table:\n");
        int counter = 0;
        for(List<Quad> b : buckets) {
            if(!b.isEmpty()){
                s.append(counter).append(" :");
                for(Quad quad : b){
                    s.append(String.format(" %s",quad.toString()));
                }
                s.append("\n");
            }
            counter++;
        }
        return s.toString();
    }
}