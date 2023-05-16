package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;

import java.util.Objects;

public class Quad{
    private String id;
    private Object value;
    private Type type;
    private Nature nature;

    public Quad(String id, Object value, Type type, Nature nature) throws TypeException {
        if(value!="OMEGA" && !isValid(id,value,type,nature)){
            throw new TypeException(String.format("The value %s is invalid for the type %s",value, type));
        }
        this.id = id;
        this.value = value;
        this.nature = nature;
        this.type = type;
    }

    // Methods

    private boolean isValid(String id, Object value, Type type, Nature nature){
        if(value == null || id == null || nature == null || type == null){
            return true;
        }
        if(nature.equals(Nature.METH)){
            return true;
        }
        if(value == "OMEGA"){
            return true;
        }
        if(type.equals(Type.OMEGA)){
            return true;
        }
        if(nature.equals(Nature.TAB)){
            return value instanceof Integer;
        }
        if(nature.equals(Nature.METH)){
            return true;
            //return value instanceof Object[];
        }
        if(type.equals(Type.INTEGER) && !(value instanceof Integer)){
            return false;
        }
        if(type.equals(Type.BOOLEAN) && !(value instanceof Boolean)){
            return false;
        }
        return type.toString().equalsIgnoreCase(value.getClass().getSimpleName());
        // TODO : to complete
    }

    // Getters

    public String getID(){
        return id;
    }

    public Object getValue(){
        return value;
    }

    public Type getType(){
        return type;
    }

    public Nature getNature(){
        return nature;
    }

    // Setters
    public void setID(String id) {
        this.id = id;
    }

    public void setValue(Object value){
        this.value = value;
    }

    public void setNature(Nature nature){
        this.nature = nature;
    }

    public void setType(Type type){
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(this.getClass() != obj.getClass()){
            return false;
        }
        Quad o = (Quad) obj;

        if(value == null && o.value != null){
            return false;
        }

        if(value != null && o.value == null){
            return false;
        }

        return Objects.equals(o.value, value) && o.getNature() == nature && o.type == type && o.id.equals(id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "<" + ((id != null) ? id : Type.toString(Type.OMEGA)) +
                ", " +
                ((value != null) ? value : Type.toString(Type.OMEGA)) +
                ", " +
                ((nature != null) ? nature : Type.toString(Type.OMEGA)) +
                ", " +
                ((type != null) ? type : Type.toString(Type.OMEGA)) +
                ">";
    }
}