package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SymbolTableUnitTest {

    private SymbolTable symbolTable;
    private Quad quad;

    @Before
    public void setUp() throws TypeException {
        symbolTable = new SymbolTable();
        quad = new Quad("i@main",42, Type.INTEGER, Nature.VAR);
    }

    @Test
    public void putOneElement() throws SymbolException {
        int hashCode = SymbolTable.hash(quad.getID());
        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode).size());
        symbolTable.put(quad);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode).size());
        Assert.assertEquals(quad,symbolTable.getBuckets().get(hashCode).get(0));
    }

    @Test
    public void putDuplicatedID() throws SymbolException {
        int hashCode = SymbolTable.hash(quad.getID());
        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode).size());
        symbolTable.put(quad);
        Assert.assertThrows(
                SymbolException.class,
                ()-> symbolTable.put(new Quad(quad.getID(), 42, Type.INTEGER, Nature.VAR))
        );
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode).size());
    }

    @Test
    public void putManyElements() throws TypeException, SymbolException {
        Quad quad2 = new Quad("1",true,Type.BOOLEAN,Nature.CST);
        Quad quad3 = new Quad("2",123,Type.INTEGER,Nature.TAB);
        int hashCode1 = SymbolTable.hash(quad.getID());
        int hashCode2 = SymbolTable.hash(quad2.getID());
        int hashCode3 = SymbolTable.hash(quad3.getID());

        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode1).size());
        symbolTable.put(quad);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode1).size());
        symbolTable.put(quad2);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode2).size());
        symbolTable.put(quad3);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode3).size());
    }

    @Test
    public void putManyElementsSameHashCode() throws SymbolException, TypeException {
        Quad quad1 = new Quad("salut",1,Type.INTEGER,Nature.CST);
        Quad quad2 = new Quad("tsb",2,Type.INTEGER,Nature.CST);
        Quad quad3 = new Quad("PSRbr",3,Type.INTEGER,Nature.CST);

        int hashCode1 = SymbolTable.hash(quad1.getID());
        int hashCode2 = SymbolTable.hash(quad2.getID());
        int hashCode3 = SymbolTable.hash(quad3.getID());

        Assert.assertEquals(hashCode1,hashCode2);
        Assert.assertEquals(hashCode2,hashCode3);

        symbolTable.put(quad1);
        symbolTable.put(quad2);
        symbolTable.put(quad3);

        Assert.assertEquals(3,symbolTable.getBuckets().get(hashCode1).size());
    }

    @Test
    public void putSameElementManyTimes() throws TypeException, SymbolException {
        Quad quad = new Quad("salut@main",1,Type.INTEGER,Nature.CST);
        int hashCode = SymbolTable.hash(quad.getID());
        symbolTable.put(quad);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode).size());
        Assert.assertThrows(
                SymbolException.class,
                ()-> symbolTable.put(quad)
        );
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode).size());
    }

    @Test
    public void getOneElement() throws SymbolException {
        symbolTable.put(quad);
        Assert.assertEquals(quad,symbolTable.get("i@main"));
    }

    @Test
    public void getOneUnknownElement() {
        Assert.assertThrows(
                SymbolException.class,
                ()-> symbolTable.get("i@main")
        );
    }

    @Test
    public void getManyElementsSameHashCode() throws TypeException, SymbolException {
        Quad quad1 = new Quad("salut",1,Type.INTEGER,Nature.CST);
        Quad quad2 = new Quad("tsb",2,Type.INTEGER,Nature.CST);
        Quad quad3 = new Quad("PSRbr",3,Type.INTEGER,Nature.CST);

        int hashCode1 = SymbolTable.hash(quad1.getID());
        int hashCode2 = SymbolTable.hash(quad2.getID());
        int hashCode3 = SymbolTable.hash(quad3.getID());

        Assert.assertEquals(hashCode1,hashCode2);
        Assert.assertEquals(hashCode2,hashCode3);

        symbolTable.put(quad1);
        symbolTable.put(quad2);
        symbolTable.put(quad3);
        Assert.assertEquals(3,symbolTable.getBuckets().get(hashCode1).size());

        Assert.assertEquals(quad1,symbolTable.get("salut"));
        Assert.assertEquals(quad2,symbolTable.get("tsb"));
        Assert.assertEquals(quad3,symbolTable.get("PSRbr"));
    }

    @Test
    public void deleteOneElement() throws SymbolException {
        int hashCode = SymbolTable.hash(quad.getID());
        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode).size());
        symbolTable.put(quad);
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode).size());

        Assert.assertEquals(quad,symbolTable.delete("i@main"));
        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode).size());
    }

    @Test (expected = SymbolException.class)
    public void deleteOneUnknownElement() throws SymbolException {
        symbolTable.delete("i@main");
    }

    @Test
    public void deleteElementSameHashCode() throws TypeException, SymbolException {
        Quad quad1 = new Quad("salut",1,Type.INTEGER,Nature.CST);
        Quad quad2 = new Quad("tsb",2,Type.INTEGER,Nature.CST);
        Quad quad3 = new Quad("PSRbr",3,Type.INTEGER,Nature.CST);

        int hashCode1 = SymbolTable.hash(quad1.getID());
        int hashCode2 = SymbolTable.hash(quad2.getID());
        int hashCode3 = SymbolTable.hash(quad3.getID());

        Assert.assertEquals(hashCode1,hashCode2);
        Assert.assertEquals(hashCode2,hashCode3);

        symbolTable.put(quad1);
        symbolTable.put(quad2);
        symbolTable.put(quad3);
        Assert.assertEquals(3,symbolTable.getBuckets().get(hashCode1).size());

        Assert.assertEquals(quad2,symbolTable.delete("tsb"));
        Assert.assertEquals(2,symbolTable.getBuckets().get(hashCode1).size());

        Assert.assertEquals(quad1,symbolTable.delete("salut"));
        Assert.assertEquals(1,symbolTable.getBuckets().get(hashCode2).size());

        Assert.assertEquals(quad3,symbolTable.delete("PSRbr"));
        Assert.assertEquals(0,symbolTable.getBuckets().get(hashCode3).size());
    }

    @Test
    public void updateIntegerEntry() throws SymbolException, TypeException {
        symbolTable.put(quad);
        Assert.assertEquals(Integer.class,quad.getValue().getClass());
        Assert.assertEquals(42,quad.getValue());
        Assert.assertEquals("i@main",quad.getID());
        symbolTable.update("i@main",123);
        Assert.assertEquals(Integer.class,quad.getValue().getClass());
        Assert.assertEquals(123,quad.getValue());
    }

    @Test
    public void updateBooleanEntry() throws TypeException, SymbolException {
        Quad quad2 = new Quad("1",true, Type.BOOLEAN, Nature.VAR);
        symbolTable.put(quad2);
        Assert.assertEquals(Boolean.class,quad2.getValue().getClass());
        Assert.assertEquals(true,quad2.getValue());
        Assert.assertEquals("1",quad2.getID());
        symbolTable.update("1",false);
        Assert.assertEquals(Boolean.class,quad2.getValue().getClass());
        Assert.assertEquals(false,quad2.getValue());
    }

    @Test
    public void updateIntegerEntryInvalidType() throws SymbolException {
        symbolTable.put(quad);
        Assert.assertEquals(Integer.class,quad.getValue().getClass());
        Assert.assertEquals(42,quad.getValue());
        Assert.assertEquals("i@main",quad.getID());
        Assert.assertThrows(
                TypeException.class,
                ()-> symbolTable.update("i@main",false)
        );
    }

    @Test
    public void updateBooleanEntryInvalidType() throws TypeException, SymbolException {
        Quad quad2 = new Quad("1",true, Type.BOOLEAN, Nature.VAR);
        symbolTable.put(quad2);
        Assert.assertEquals(Boolean.class,quad2.getValue().getClass());
        Assert.assertEquals(true,quad2.getValue());
        Assert.assertEquals("1",quad2.getID());
        Assert.assertThrows(
                TypeException.class,
                ()-> symbolTable.update("1",123)
        );
    }

    // TODO Type Checking

    @Test
    public void toStringSimple() throws TypeException, SymbolException {
        Quad quad1 = new Quad("1",1,Type.INTEGER,Nature.CST);
        Quad quad2 = new Quad("2",true,Type.BOOLEAN,Nature.CST);
        Quad quad3 = new Quad("3",3,Type.INTEGER,Nature.CST);
        Quad quad4 = new Quad("4",42,Type.INTEGER,Nature.VAR);

        symbolTable.put(quad1);
        symbolTable.put(quad2);
        symbolTable.put(quad3);
        symbolTable.put(quad4);

        String s = "Symbol table:\n" +
                "22 : <1, 1, CST, INTEGER>\n" +
                "23 : <2, true, CST, BOOLEAN>\n" +
                "24 : <3, 3, CST, INTEGER>\n" +
                "25 : <4, 42, VAR, INTEGER>\n";
        Assert.assertEquals(s, symbolTable.toString());
    }

    @Test
    public void toStringSameHashcode() throws TypeException, SymbolException {
        Quad quad1 = new Quad("salut",1,Type.INTEGER,Nature.CST);
        Quad quad2 = new Quad("tsb",2,Type.INTEGER,Nature.CST);
        Quad quad3 = new Quad("PSRbr",3,Type.INTEGER,Nature.CST);

        symbolTable.put(quad1);
        symbolTable.put(quad2);
        symbolTable.put(quad3);

        String s = "Symbol table:\n" + "14 : <salut, 1, CST, INTEGER> <tsb, 2, CST, INTEGER> <PSRbr, 3, CST, INTEGER>\n";

        Assert.assertEquals(s,symbolTable.toString());

    }
}