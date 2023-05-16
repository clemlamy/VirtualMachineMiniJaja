package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QuadTest {
    static Quad quad1,quad2,quad3,quad4,quad5;
    static Integer array_heap;

    @Before
    public void initQuads() throws TypeException {
        array_heap = 1;
        quad1 = new Quad("1",1, Type.INTEGER, Nature.CST);
        quad2 = new Quad("2",true, Type.BOOLEAN, Nature.VAR);
        quad3 = new Quad("3",null, Type.INTEGER, Nature.VCST);
        quad4 = new Quad("4", array_heap, Type.INTEGER, Nature.TAB);
        quad5 = new Quad("5",null, Type.VOID, Nature.METH);
    }

    @Test (expected = TypeException.class)
    public void test_constructor_badType() throws TypeException {
        new Quad("id","bad value", Type.INTEGER, Nature.VAR);
    }

    @Test (expected = TypeException.class)
    public void test_constructor_badType2() throws TypeException {
        new Quad("id","bad value", Type.BOOLEAN, Nature.VAR);
    }

    @Test
    public void test_getID(){
        Assert.assertEquals("1",quad1.getID());
        Assert.assertEquals("2",quad2.getID());
        Assert.assertEquals("3",quad3.getID());
        Assert.assertEquals("4",quad4.getID());
        Assert.assertEquals("5",quad5.getID());
    }

    @Test
    public void test_setID(){
        Assert.assertEquals("1",quad1.getID());
        quad1.setID("helloHello");
        Assert.assertEquals("helloHello",quad1.getID());
    }
    @Test
    public void test_getValue(){
        Assert.assertEquals(1,quad1.getValue());
        Assert.assertEquals(true,quad2.getValue());
        Assert.assertNull(quad3.getValue());
        Assert.assertEquals(array_heap,quad4.getValue());
        Assert.assertNull(quad5.getValue());
    }

    @Test
    public void test_setValue(){
        Assert.assertEquals(1,quad1.getValue());
        quad1.setValue(123456789);
        Assert.assertEquals(123456789,quad1.getValue());
    }

    @Test
    public void test_getNature(){
        Assert.assertEquals(Nature.CST,quad1.getNature());
        Assert.assertEquals(Nature.VAR,quad2.getNature());
        Assert.assertEquals(Nature.VCST,quad3.getNature());
        Assert.assertEquals(Nature.TAB,quad4.getNature());
        Assert.assertEquals(Nature.METH,quad5.getNature());
    }

    @Test
    public void test_setNature(){
        Assert.assertEquals(Nature.VCST,quad3.getNature());
        quad3.setNature(Nature.CST);
        Assert.assertEquals(Nature.CST,quad3.getNature());
    }

    @Test
    public void test_getType(){
        Assert.assertEquals(Type.INTEGER,quad1.getType());
        Assert.assertEquals(Type.BOOLEAN,quad2.getType());
        Assert.assertEquals(Type.VOID,quad5.getType());
    }

    @Test
    public void test_setType(){
        Assert.assertEquals(Type.VOID,quad5.getType());
        quad5.setType(Type.BOOLEAN);
        Assert.assertEquals(Type.BOOLEAN,quad5.getType());
    }

    @Test
    public void test_toString() throws TypeException {
        Assert.assertEquals("<?, 1, VAR, INTEGER>", new Quad(null, 1, Type.INTEGER, Nature.VAR).toString());
        Assert.assertEquals("<1, ?, VAR, INTEGER>", new Quad("1", null, Type.INTEGER, Nature.VAR).toString());
        Assert.assertEquals("<1, 1, VAR, ?>", new Quad("1", 1, null, Nature.VAR).toString());
        Assert.assertEquals("<1, 1, ?, INTEGER>", new Quad("1", 1, Type.INTEGER, null).toString());
    }

    @Test
    public void test_equals() throws TypeException {
        Quad q = new Quad("1",1, Type.INTEGER, Nature.CST);
        Assert.assertEquals(quad1, q);
        Quad q1 = new Quad(Type.toString(Type.OMEGA), "Hello !" , Type.OMEGA ,Nature.VAR);
        Quad q2 = new Quad(Type.toString(Type.OMEGA), "Hello !" , Type.OMEGA ,Nature.VAR);
        Assert.assertEquals(q1,q2);
    }

    @Test
    public void test_equals_same() {
        Assert.assertEquals(quad1, quad1);
    }

    @Test
    public void test_notEquals() throws TypeException {
        Quad q = new Quad("1",2, Type.INTEGER, Nature.CST);
        Assert.assertNotEquals(quad1,q);
        Assert.assertNotEquals(quad1,"hello");
    }

    @Test
    public void test_notEquals_null() {
        Assert.assertNotEquals(quad1,null);
        Assert.assertNotEquals(quad1,quad3);
        Assert.assertNotEquals(quad3,quad1);
    }

    @Test
    public void test_equals_vcst() throws TypeException {
        Quad q = new Quad("3",null, Type.INTEGER, Nature.VCST);
        Assert.assertEquals(quad3,q);
    }

    @Test
    public void test_hash() throws TypeException {
        Quad q = new Quad("3",null, Type.INTEGER, Nature.VCST);
        Assert.assertNotEquals(q.hashCode(),new Quad("3",null, Type.INTEGER, Nature.VCST).hashCode());
    }

}
