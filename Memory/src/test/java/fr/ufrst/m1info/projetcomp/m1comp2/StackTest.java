package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StackTest {
    static Stack stack;
    static Quad quad1,quad2,quad3,quad4, quad5;

    @Before
    public void setup() throws TypeException {
        stack = new Stack();
        quad1 = new Quad("1",1, Type.INTEGER, Nature.CST);
        quad2 = new Quad("2",true, Type.BOOLEAN, Nature.VAR);
        quad3 = new Quad("3",3, Type.INTEGER, Nature.CST);
        quad4 = new Quad("4",42, Type.INTEGER, Nature.VAR);
        quad5 = new Quad("2",false, Type.BOOLEAN, Nature.VAR);
    }

    @Test
    public void test_getBottom_empty_thenExceptionThrown(){
        Assert.assertThrows(
                StackException.class,
                () -> stack.getFromTop(42)
        );
    }

    @Test
    public void test_getBottom() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);

        Assert.assertEquals(quad1, stack.getBottom());
    }

    @Test
    public void test_isEmpty_empty_thenReturnTrue(){
        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void test_isEmpty_notEmpty_thenReturnFalse(){
        stack.enqueue(quad1);
        Assert.assertFalse(stack.isEmpty());
    }

    @Test
    public void test_getValues(){
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        Assert.assertEquals(quad1, stack.getValues().get(0));
        Assert.assertEquals(quad2, stack.getValues().get(1));
        Assert.assertEquals(quad3, stack.getValues().get(2));
        Assert.assertEquals(quad4, stack.getValues().get(3));
    }


    @Test
    public void test_getFromTop_emptyStack_stackExceptionThrown() {
        Assert.assertThrows(
          StackException.class,
                () -> stack.getFromTop(42)
        );
    }

    @Test
    public void test_getFromTop_invalidIndex_thenIndexOutOfBoundsThrown() {
        stack.enqueue(quad1);
        Assert.assertThrows(
                IndexOutOfBoundsException.class,
                () -> stack.getFromTop(42)
        );
    }

    @Test
    public void test_getFromTop_validIndex() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        Assert.assertEquals(quad1, stack.getFromTop(3));
        Assert.assertEquals(quad2, stack.getFromTop(2));
        Assert.assertEquals(quad3, stack.getFromTop(1));
        Assert.assertEquals(quad4, stack.getFromTop(0));
    }

    @Test
    public void test_peek_empty_thenStackExceptionThrown() {
        Assert.assertThrows(
                StackException.class,
                () -> stack.peek()
        );
    }

    @Test
    public void test_peek_thenTopElementReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        Assert.assertEquals(quad4, stack.peek());
    }

    @Test
    public void test_enqueue() throws StackException {
        Assert.assertTrue(stack.isEmpty());
        stack.enqueue(quad1);
        Assert.assertEquals(quad1,stack.peek());
        stack.enqueue(quad2);
        Assert.assertEquals(quad2,stack.peek());
        stack.enqueue(quad3);
        Assert.assertEquals(quad3,stack.peek());
        stack.enqueue(quad4);
        Assert.assertEquals(quad4,stack.peek());
    }

    @Test
    public void test_dequeue_empty_thenStackExceptionThrown() {
        Assert.assertThrows(
                StackException.class,
                () -> stack.dequeue()
        );
    }

    @Test
    public void test_dequeue() throws StackException {
        Assert.assertTrue(stack.isEmpty());
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        Assert.assertEquals(quad4,stack.peek());
        Assert.assertEquals(quad4, stack.dequeue());
        Assert.assertEquals(quad3,stack.peek());
        Assert.assertEquals(quad3, stack.dequeue());
        Assert.assertEquals(quad2,stack.peek());
        Assert.assertEquals(quad2, stack.dequeue());
        Assert.assertEquals(quad1,stack.peek());
        Assert.assertEquals(quad1, stack.dequeue());
        Assert.assertTrue(stack.isEmpty());
    }

    @Test
    public void test_swap_empty_thenStackExceptionThrown() {
        Assert.assertThrows(
                StackException.class,
                () -> stack.swap()
        );
    }

    @Test
    public void test_swap_onlyOne_thenStackExceptionThrown() {
        stack.enqueue(quad1);
        Assert.assertThrows(
                StackException.class,
                () -> stack.swap()
        );
    }

    @Test
    public void test_swap_items_thenItemsSwapped() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        Assert.assertEquals(quad2,stack.peek());
        Assert.assertEquals(quad1, stack.getFromTop(1));
        Assert.assertEquals(quad2, stack.getFromTop(0));
        stack.swap();
        Assert.assertEquals(quad1,stack.peek());
        Assert.assertEquals(quad2, stack.getFromTop(1));
        Assert.assertEquals(quad1, stack.getFromTop(0));
    }

    @Test
    public void test_toString(){
        Assert.assertEquals(quad1, stack.enqueue(quad1));
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        String s = "Stack:\n" + "\t<1, 1, CST, INTEGER>\n" +
                "\t<2, true, VAR, BOOLEAN>\n" +
                "\t<3, 3, CST, INTEGER>\n" +
                "\t<4, 42, VAR, INTEGER>\n";
        System.out.println(s);
        System.out.println(stack.toString());
        Assert.assertEquals(s, stack.toString());
    }

    @Test
    public void test_getFirstOf_notExists_thenExceptionThrown() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);

        Assert.assertThrows(
                StackException.class,
                () -> stack.getFirstOf("not exists")
        );
    }

    @Test
    public void test_getFirstOf_oneExists_thenReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);

        Assert.assertEquals(quad2,stack.getFirstOf("2"));
    }

    @Test
    public void test_getFirstOf_severalExists_thenReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        stack.enqueue(quad5);

        Assert.assertEquals(quad5,stack.getFirstOf("2"));
    }


    @Test
    public void test_removeFirstOf_notExists_thenNullReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);

        Assert.assertNull(stack.removeFirstOf("not exists"));
    }

    @Test
    public void test_removeFirstOf_oneExists_thenReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);

        Assert.assertEquals(quad2,stack.removeFirstOf("2"));
    }

    @Test
    public void test_removeFirstOf_severalExists_thenReturned() throws StackException {
        stack.enqueue(quad1);
        stack.enqueue(quad2);
        stack.enqueue(quad3);
        stack.enqueue(quad4);
        stack.enqueue(quad5);

        Assert.assertEquals(quad5,stack.removeFirstOf("2"));
    }
}
