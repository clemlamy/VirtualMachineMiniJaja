package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.jjc;

import fr.ufrst.m1info.projetcomp.m1comp2.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.*;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.InterpreterMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.JajaCodeTreeConstants.*;

public class InterpretorJjcVisitorTest {

    private InterpreterJjcVisitor interpreterVisitor;
    private Memory memory;

    private SymbolTable symbolTable;

    @Before
    public void init() {
        interpreterVisitor = new InterpreterJjcVisitor();
        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);

        interpreterVisitor.setMemory(memory);
        interpreterVisitor. setAddress(1);
    }

    private void push(int value) throws TypeException {
        memory.getStack().enqueue(new Quad(
                Type.toString(Type.OMEGA),
                value,
                Type.INTEGER,
                Nature.CST)
        );
    }

    private void push(boolean value) throws TypeException {
        memory.getStack().enqueue(new Quad(
                Type.toString(Type.OMEGA),
                value,
                Type.BOOLEAN,
                Nature.CST)
        );
    }

    private void push(Object o) throws TypeException {
        memory.getStack().enqueue(new Quad(
                Type.toString(Type.OMEGA),
                o,
                Type.INTEGER,
                Nature.VAR)
        );
    }

    @Test
    public void nodeInitTest() throws VisitorException {
        InterpreterJjcVisitor interpretVisitor = new InterpreterJjcVisitor();
        ASTInit nodeInit = new ASTInit(JJTINIT);
        Object res = nodeInit.jjtAccept(interpretVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpretVisitor.getAddress());
    }


    @Test
    public void nodePopTest() throws VisitorException, TypeException, StackException {
        push(4);
        push(5);

        ASTPop nodePop = new ASTPop(JJTPOP);
        Object res = nodePop.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
    }

    @Test(expected = VisitorException.class)
    public void nodePopNonExistingValueTest() throws VisitorException {

        //creation of jjc AST
        ASTPop nodePop = new ASTPop(JJTPOP);

        //interpretation
        nodePop.jjtAccept(interpreterVisitor, null);

    }

    @Test
    public void nodePushNumberTest() throws VisitorException, StackException {
        ASTPush nodePush = new ASTPush(JJTPUSH);
        ASTJCNbre n = new ASTJCNbre(JJTJCNBRE);
        n.jjtSetValue(4);
        nodePush.jjtAddChild(n, 0);

        //interpretation
        Object res = nodePush.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
    }

    @Test
    public void nodePushTrueTest() throws VisitorException, StackException {
        ASTPush nodePush = new ASTPush(JJTPUSH);
        nodePush.jjtAddChild(new ASTJCVrai(JJTJCVRAI), 0);

        //interpretation
        Object res = nodePush.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
    }

    @Test
    public void nodePushFalseTest() throws VisitorException, StackException {
        ASTPush nodePush = new ASTPush(JJTPUSH);
        nodePush.jjtAddChild(new ASTJCFaux(JJTJCFAUX), 0);

        //interpretation
        Object res = nodePush.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }


    @Test(expected = VisitorException.class)
    public void nodeSwapEmptyStackTest() throws VisitorException {

        //creation of jjc AST
        ASTSwap nodeSwap = new ASTSwap(JJTSWAP);

        //interpretation
        nodeSwap.jjtAccept(interpreterVisitor, null);
    }

    @Test
    public void nodeSwapTest() throws VisitorException, TypeException, StackException {

        push(4);
        push(true);

        ASTSwap nodeSwap = new ASTSwap(JJTSWAP);

        //interpretation
        Object res = nodeSwap.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertTrue((Boolean) memory.getStack().getFromTop(0).getValue());
    }

    @Test
    public void nodeIdentTest() throws VisitorException {

        ASTJCIdent nodeIdent = new ASTJCIdent(JJTJCIDENT);
        nodeIdent.jjtSetValue("ident");

        //interpretation
        Object res = nodeIdent.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals("ident", res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeTypeTest() throws VisitorException {
        //creation of jjc AST
        ASTJCType nodeType = new ASTJCType(JJTJCTYPE);
        nodeType.jjtSetValue("entier");
        //interpretation
        Object res = nodeType.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals(Type.INTEGER, res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }


    @Test
    public void nodeKindTest() throws VisitorException {
        //creation of jjc AST
        ASTJCSorte nodeKind = new ASTJCSorte(JJTJCSORTE);
        nodeKind.jjtSetValue("var");
        //interpretation
        Object res = nodeKind.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals(Nature.VAR, res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }


    @Test
    public void nodeNumberTest() throws VisitorException {
        //creation of jjc AST
        ASTJCNbre nodeNumber = new ASTJCNbre(JJTJCNBRE);
        nodeNumber.jjtSetValue(4);
        //interpretation
        Object res = nodeNumber.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals(4, res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeFalseTest() throws VisitorException {
        //creation of jjc AST
        ASTJCFaux nodeFalse = new ASTJCFaux(JJTJCFAUX);

        //interpretation
        Object res = nodeFalse.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals(false, res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeTrueTest() throws VisitorException {
        //creation of jjc AST
        ASTJCVrai nodeTrue = new ASTJCVrai(JJTJCVRAI);

        //interpretation
        Object res = nodeTrue.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertEquals(true, res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeJcnilTest() throws VisitorException {
        //creation of jjc AST
        ASTJCNil nodeJcnil = new ASTJCNil(JJTJCNIL);

        //interpretation
        Object res = nodeJcnil.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(1, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeJcstopTest() throws VisitorException {
        //creation of jjc AST
        ASTJCStop nodeJcstop = new ASTJCStop(JJTJCSTOP);

        //interpretation
        Object res = nodeJcstop.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(-1, interpreterVisitor.getAddress());
    }


// KO: Bad cast Nature
//    //incorrect s  argument : no value in stack at position 0 from top (because stack is empty)
    @Test(expected = VisitorException.class)
    public void nodeNewTestVar1() throws VisitorException {

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue("var");
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);
    }

//    //no corresponding symbol in symbolTable
    /*@Test(expected = VisitorException.class)
    public void nodeNewTestVar2() throws VisitorException, TypeException {

        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);
    }*/

    //type don't match with the symbol's type
    @Ignore
    @Test(expected = VisitorException.class)
    public void nodeNewTestVar2() throws VisitorException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", false, Type.BOOLEAN, Nature.VAR));
        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue("var");
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);
        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);
    }

    @Test(expected = SymbolException.class)
    public void nodeNewNoValue() throws VisitorException, TypeException, SymbolException {
        push("OMEGA");

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue("var");
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);
        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);
        memory.getValue("x0");
    }

    /*@Test
    public void nodeNewTestVar4() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);
        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);

        //interpretation
        Object res = nodeNew.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
    }

    //new unassigned var
    @Test
    public void nodeNewTestVar5() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(null);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        Object res = nodeNew.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertNull(memory.getStack().getFromTop(0).getValue());
    }*/

    //incorrect s  argument : no value in stack at position 0 from top (because stack is empty)
    @Test
    public void nodeNewTestCst1() throws VisitorException {

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue("cst");
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeNew.jjtAccept(interpreterVisitor, null)
        );
    }

    @Test
    public void nodeNewMethode_exceptionThrown(){
        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue("meth");
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        Assert.assertThrows(
                VisitorException.class,
                () -> nodeNew.jjtAccept(interpreterVisitor, null)
        );
    }

/*    //no corresponding symbol in symbolTable
    @Test
    public void nodeNewTestCst2() throws VisitorException, TypeException, StackException {

        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.CST);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        Object res = nodeNew.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertNull(memory.getStack().getFromTop(0).getValue());
    }

    //type don't match with the symbol's type
    @Test(expected = VisitorException.class)
    public void nodeNewTestCst3() throws VisitorException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", true, Type.BOOLEAN, Nature.CST));
        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        nodeNew.jjtAccept(interpreterVisitor, null);
    }


    @Test
    public void nodeNewTestCst4() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 4, Type.INTEGER, Nature.CST));
        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        Object res = nodeNew.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
    }

    //new unassigned constant
    @Test
    public void nodeNewTestCst5() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 4, Type.INTEGER, Nature.CST));
        push(4);

        ASTNew nodeNew = new ASTNew(JJTNEW);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeNew.jjtAddChild(ident, 0);

        ASTJCType type = new ASTJCType(JJTJCTYPE);
        type.jjtSetValue("entier");
        nodeNew.jjtAddChild(type, 1);

        ASTJCSorte nature = new ASTJCSorte(JJTJCSORTE);
        nature.jjtSetValue(Nature.VAR);
        nodeNew.jjtAddChild(nature, 2);

        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(0);
        nodeNew.jjtAddChild(nbre, 3);

        //interpretation
        Object res = nodeNew.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertNull(memory.getStack().getFromTop(0).getValue());
    }*/

    @Test
    public void nodeAddTest() throws VisitorException, StackException, TypeException {

        push(false);
        push(3);
        push(4);

        //-----------------

        //creation of jjc AST
        ASTAdd nodeAdd = new ASTAdd(JJTADD);

        //interpretation
        Object res = nodeAdd.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(7, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }


    //Miss value in stack
    @Test(expected = VisitorException.class)
    public void nodeAddExceptionTest() throws VisitorException, StackException, TypeException {

        push(false);
        push(4);

        //-----------------

        //creation of jjc AST
        ASTAdd nodeAdd = new ASTAdd(JJTADD);

        //interpretation
        Object res = nodeAdd.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(7, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }


    //division result is an integer
    @Test
    public void nodeDivTest1() throws VisitorException, StackException, TypeException {

        push(false);
        push(6);
        push(2);

        //-----------------

        //creation of jjc AST
        ASTDiv nodeDiv = new ASTDiv(JJTDIV);

        //interpretation
        Object res = nodeDiv.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }

    //division result is not an integer
    @Test
    public void nodeDivTest2() throws VisitorException, StackException, TypeException {

        push(false);
        push(5);
        push(2);

        //-----------------

        //creation of jjc AST
        ASTDiv nodeDiv = new ASTDiv(JJTDIV);

        //interpretation
        Object res = nodeDiv.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(2, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }

    //division by 0
    @Test(expected = VisitorException.class)
    public void nodeDivTest3() throws VisitorException, TypeException {

        push(false);
        push(5);
        push(0);

        //-----------------

        //creation of jjc AST
        ASTDiv nodeDiv = new ASTDiv(JJTDIV);

        //interpretation
        nodeDiv.jjtAccept(interpreterVisitor, null);
    }

    @Test
    public void nodeMulTest() throws VisitorException, StackException, TypeException {

        push(false);
        push(6);
        push(2);

        //-----------------

        //creation of jjc AST
        ASTMul nodeMul = new ASTMul(JJTMUL);

        //interpretation
        Object res = nodeMul.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(12, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }

    //Missing a value in stack
    @Test(expected = VisitorException.class)
    public void nodeMulExceptionTest() throws VisitorException, TypeException {

        push(2);

        //creation of jjc AST
        ASTMul nodeMul = new ASTMul(JJTMUL);

        //interpretation
        nodeMul.jjtAccept(interpreterVisitor, null);

    }

    @Test
    public void ASTNopTest() throws VisitorException {

        ASTNop nodeNop = new ASTNop(JJTNOP);

        Object res = nodeNop.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);


        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());


    }
    //second operand is positive

    @Test
    public void nodeSubTest1() throws VisitorException, StackException, TypeException {

        push(false);
        push(6);
        push(2);

        //-----------------

        //creation of jjc AST
        ASTSub nodeSub = new ASTSub(JJTSUB);

        //interpretation
        Object res = nodeSub.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(4, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }
    //second operand is negative

    @Test
    public void nodeSubTest2() throws VisitorException, StackException, TypeException {

        push(false);
        push(6);
        push(-2);

        //-----------------

        //creation of jjc AST
        ASTSub nodeSub = new ASTSub(JJTSUB);

        //interpretation
        Object res = nodeSub.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(8, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }
    //missing value on stack

    @Test(expected = VisitorException.class)
    public void nodeSubExceptionTest() throws VisitorException, TypeException {

        push(6);


        //creation of jjc AST
        ASTSub nodeSub = new ASTSub(JJTSUB);

        //interpretation
        nodeSub.jjtAccept(interpreterVisitor, null);
    }
    //on positive integer

    @Test
    public void nodeNegTest1() throws VisitorException, StackException, TypeException {

        push(false);
        push(6);

        //-----------------

        //creation of jjc AST
        ASTNeg nodeNeg = new ASTNeg(JJTNEG);

        //interpretation
        Object res = nodeNeg.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(-6, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }
    //on negative integer

    @Test
    public void nodeNegTest2() throws VisitorException, StackException, TypeException {

        push(false);
        push(-6);

        //-----------------

        //creation of jjc AST
        ASTNeg nodeNeg = new ASTNeg(JJTNEG);

        //interpretation
        Object res = nodeNeg.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(6, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }
    //Empty stack

    @Test(expected = VisitorException.class)
    public void nodeNegExceptionTest() throws VisitorException{

        //creation of jjc AST
        ASTNeg nodeNeg = new ASTNeg(JJTNEG);

        //interpretation
        nodeNeg.jjtAccept(interpreterVisitor, null);

    }
    //variable x has not been declared before

    @Test(expected = VisitorException.class)
    public void nodeIncTest1() throws VisitorException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));

        push(false);
        push(3);

        ASTInc nodeInc = new ASTInc(JJTINC);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeInc.jjtAddChild(ident, 0);

        //interpretation
        nodeInc.jjtAccept(interpreterVisitor, null);
    }
    //variable x has not been initialized

    @Test(expected = VisitorException.class)
    public void nodeIncExceptioncTest() throws VisitorException, TypeException, SymbolException, StackException {

        push(null);
        memory.identVal("x0", Type.INTEGER, 0);

        push(2);

        ASTInc nodeInc = new ASTInc(JJTINC);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeInc.jjtAddChild(ident, 0);

        //interpretation
        nodeInc.jjtAccept(interpreterVisitor, null);
    }

    @Ignore
    @Test
    public void nodeIncTest2() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 2, Type.INTEGER, Nature.VAR));
        push(null);
        memory.identVal("x0", Type.INTEGER, 0);

        push(false);
        push(3);

        ASTInc nodeInc = new ASTInc(JJTINC);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeInc.jjtAddChild(ident, 0);

        //interpretation
        Object res = nodeInc.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(5, (int) memory.getStack().getFromTop(0).getValue());
    }
    //variable x has not been declared before

    @Test(expected = VisitorException.class)
    public void nodeLoadTest1() throws VisitorException, TypeException, SymbolException {

        //symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));

        push(false);
        push(3);

        ASTLoad nodeLoad = new ASTLoad(JJTLOAD);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeLoad.jjtAddChild(ident, 0);

        //interpretation
        nodeLoad.jjtAccept(interpreterVisitor, null);
    }
    //variable x has not been initialized before

    @Test(expected = VisitorException.class)
    public void nodeLoadExceptionTest() throws VisitorException, TypeException, SymbolException, StackException {

        //symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(null);
        memory.identVal("x0", Type.INTEGER, 0);

        ASTLoad nodeLoad = new ASTLoad(JJTLOAD);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeLoad.jjtAddChild(ident, 0);

        //interpretation
        nodeLoad.jjtAccept(interpreterVisitor, null);
    }

    @Ignore
    @Test
    public void nodeLoadTest2() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 2, Type.INTEGER, Nature.VAR));
        push(2);
        memory.identVal("x0", Type.INTEGER, 0);

        push(false);

        ASTLoad nodeLoad = new ASTLoad(JJTLOAD);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeLoad.jjtAddChild(ident, 0);

        //interpretation
        Object res = nodeLoad.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(2, (int) memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
    }
    //x0 has not been declared

    @Test(expected = VisitorException.class)
    public void nodeStoreTest1() throws VisitorException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));

        push(false);

        push(47);

        ASTStore nodeStore = new ASTStore(JJTSTORE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeStore.jjtAddChild(ident, 0);

        //interpretation
        nodeStore.jjtAccept(interpreterVisitor, null);
    }
    //store boolean in integer

    @Ignore
    @Test(expected = VisitorException.class)
    public void nodeStoreTest2() throws VisitorException, TypeException, SymbolException, StackException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(2);
        memory.identVal("x0", Type.INTEGER, 0);

        push(false);

        push(true);

        ASTStore nodeStore = new ASTStore(JJTSTORE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeStore.jjtAddChild(ident, 0);

        //interpretation
        nodeStore.jjtAccept(interpreterVisitor, null);
    }

    @Ignore
    @Test
    public void nodeStoreTest3() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(2);
        memory.identVal("x0", Type.INTEGER, 0);

        push(false);

        push(47);

        ASTStore nodeStore = new ASTStore(JJTSTORE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeStore.jjtAddChild(ident, 0);

        //interpretation
        Object res = nodeStore.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(47, (int) memory.getStack().getFromTop(0).getValue());
        //TODO : check more in detail the stack top is x0 with value 47
    }
    //store in unassigned constant

    @Ignore
    @Test
    public void nodeStoreTest4() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.VAR));
        push(null);
        memory.identVal("x0", Type.INTEGER, 0);

        push(false);

        push(47);

        ASTStore nodeStore = new ASTStore(JJTSTORE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeStore.jjtAddChild(ident, 0);

        //interpretation
        Object res = nodeStore.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(47, (int) memory.getStack().getFromTop(0).getValue());
        //TODO : check more in detail the stack top is x0 with value 47 of kind constant
    }
    //store in assigned constant

    @Test(expected = VisitorException.class)
    public void nodeStoreTest5() throws VisitorException, StackException, TypeException, SymbolException {

        symbolTable.put(new Quad("x0", 0, Type.INTEGER, Nature.CST));
        push(2);
       // memory.identVal("x0", Type.INTEGER, 0);

        push(false);

        push(47);

        ASTStore nodeStore = new ASTStore(JJTSTORE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        nodeStore.jjtAddChild(ident, 0);

        //interpretation
        nodeStore.jjtAccept(interpreterVisitor, null);
    }
    // false and true

    @Test
    public void nodeAndTest1() throws VisitorException, StackException, TypeException {

        push(3);
        push(false);
        push(true);

        //-----------------

        //creation of jjc AST
        ASTAnd nodeAnd = new ASTAnd(JJTAND);

        //interpretation
        Object res = nodeAnd.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }
    //true and true

    @Test
    public void nodeAndTest2() throws VisitorException, StackException, TypeException {

        push(3);
        push(true);
        push(true);

        ASTAnd nodeAnd = new ASTAnd(JJTAND);

        //interpretation
        Object res = nodeAnd.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }

    @Test(expected = VisitorException.class)
    public void nodeAndExceptionTest() throws VisitorException, TypeException {

        push(3);
        push(true);

        ASTAnd nodeAnd = new ASTAnd(JJTAND);

        //interpretation
        nodeAnd.jjtAccept(interpreterVisitor, null);

    }
    //false or true

    @Test
    public void nodeOrTest1() throws VisitorException, StackException, TypeException {

        push(3);
        push(false);
        push(true);

        //-----------------

        //creation of jjc AST
        ASTOr nodeOr = new ASTOr(JJTOR);

        //interpretation
        Object res = nodeOr.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }
    //false or false

    @Test
    public void nodeOrTest2() throws VisitorException, StackException, TypeException {

        push(3);
        push(false);
        push(false);


        ASTOr nodeOr = new ASTOr(JJTOR);

        //interpretation
        Object res = nodeOr.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }
    //Missing boolean value on stack

    @Test(expected = VisitorException.class)
    public void nodeOrExceptionTest() throws VisitorException, TypeException {

        push(3);
        push(false);

        ASTOr nodeOr = new ASTOr(JJTOR);

        //interpretation
        nodeOr.jjtAccept(interpreterVisitor, null);

    }
    //not false

    @Test
    public void nodeNotTest1() throws VisitorException, StackException, TypeException {

        push(3);
        push(false);

        //-----------------

        //creation of jjc AST
        ASTNot nodeNot = new ASTNot(JJTNOT);

        //interpretation
        Object res = nodeNot.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }
    //no true

    @Test
    public void nodeNotTest2() throws VisitorException, StackException, TypeException {

        push(3);
        push(true);

        ASTNot nodeNot = new ASTNot(JJTNOT);

        //interpretation
        Object res = nodeNot.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }
    //Missing value in stack

    @Test(expected = VisitorException.class)
    public void nodeNotExceptionTest() throws VisitorException{

        //creation of jjc AST
        ASTNot nodeNot = new ASTNot(JJTNOT);

        //interpretation
        nodeNot.jjtAccept(interpreterVisitor, null);
    }

    @Test
    public void nodeCmpTestNumber() throws VisitorException, StackException, TypeException {

        push(0);
        push(4);
        push(4);

        //-----------------

        //creation of jjc AST
        ASTCmp nodeCmp = new ASTCmp(JJTCMP);

        //interpretation
        Object res = nodeCmp.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }

    @Test
    public void nodeCmpTestBoolean() throws VisitorException, StackException, TypeException {

        push(3);
        push(true);
        push(false);

        //-----------------

        //creation of jjc AST
        ASTCmp nodeCmp = new ASTCmp(JJTCMP);

        //interpretation
        Object res = nodeCmp.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(3, (int) memory.getStack().getFromTop(0).getValue());
    }

    @Test(expected = VisitorException.class)
    public void nodeCmpExceptionTest() throws VisitorException, TypeException {

        push(0);

        ASTCmp nodeCmp = new ASTCmp(JJTCMP);

        //interpretation
        nodeCmp.jjtAccept(interpreterVisitor, null);

    }
    //first operand < second operand

    @Ignore
    @Test
    public void nodeSupTest1() throws VisitorException, StackException, TypeException {

        push(0);
        push(8);
        push(7);

        ASTSup nodeSup = new ASTSup(JJTSUP);

        //interpretation
        Object res = nodeSup.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //first operand == second operand

    @Test
    public void nodeSupTest2() throws VisitorException, StackException, TypeException {

        push(0);
        push(7);
        push(7);

        //-----------------

        //creation of jjc AST
        ASTSup nodeSup = new ASTSup(JJTSUP);

        //interpretation
        Object res = nodeSup.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //first operand > second operand

    @Ignore
    @Test
    public void nodeSupTest3() throws VisitorException, StackException, TypeException {

        push(0);
        push(7);
        push(8);

        //-----------------

        //creation of jjc AST
        ASTSup nodeSup = new ASTSup(JJTSUP);

        //interpretation
        Object res = nodeSup.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //Missing value on stack

    @Test(expected = VisitorException.class)
    public void nodeSupExceptionTest() throws VisitorException, TypeException {

        push(7);

        //creation of jjc AST
        ASTSup nodeSup = new ASTSup(JJTSUP);

        //interpretation
         nodeSup.jjtAccept(interpreterVisitor, null);

    }


    @Test
    public void nodeGotoTest() throws VisitorException {

        //creation of jjc AST
        ASTGoto nodeGoto = new ASTGoto(JJTGOTO);
        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(23);
        nodeGoto.jjtAddChild(nbre, 0);

        //interpretation
        Object res = nodeGoto.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(23, interpreterVisitor.getAddress());

    }

    @Test
    public void nodeIfTestTrue() throws VisitorException, TypeException {
        push(true);

        ASTIf nodeIf = new ASTIf(JJTIF);
        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(23);
        nodeIf.jjtAddChild(nbre, 0);

        //interpretation
        Object res = nodeIf.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(23, interpreterVisitor.getAddress());
    }

    @Test
    public void nodeIfTestFalse() throws VisitorException, TypeException {

        push(false);

        //-----------------

        //creation of jjc AST
        ASTIf nodeIf = new ASTIf(JJTIF);
        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(23);
        nodeIf.jjtAddChild(nbre, 0);

        //interpretation
        Object res = nodeIf.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());
    }

    @Test(expected = VisitorException.class)
    public void nodeIfExceptionTest() throws VisitorException {

        ASTIf nodeIf = new ASTIf(JJTIF);
        ASTJCNbre nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(23);
        nodeIf.jjtAddChild(nbre, 0);

        //interpretation
        nodeIf.jjtAccept(interpreterVisitor, null);

    }

    @Test
    public void nodeInvokeTest() throws StackException, TypeException, SymbolException, VisitorException {

        memory.declMethode("test@int", 1, Type.INTEGER);

        ASTInvoke nodeInvoke = new ASTInvoke(JJTINVOKE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("test@int");
        nodeInvoke.jjtAddChild(ident, 0);

        Object res = nodeInvoke.jjtAccept(interpreterVisitor, null);

        Assert.assertNull(res);

        //Check Peek Value
        Assert.assertEquals(2, (int) memory.getStack().getFromTop(0).getValue());

        //Check adress
        Assert.assertEquals(1, interpreterVisitor.getAddress());

    }

    @Test(expected = VisitorException.class)
    public void nodeInvokeExceptionTest() throws TypeException, SymbolException, VisitorException {

       // symbolTable.put(new Quad("test@int", 1, Type.INTEGER, Nature.METH));

        ASTInvoke nodeInvoke = new ASTInvoke(JJTINVOKE);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("test@int");
        nodeInvoke.jjtAddChild(ident, 0);

        nodeInvoke.jjtAccept(interpreterVisitor, null);

    }

    @Test
    public void nodeReturnTest() throws TypeException, VisitorException {

        push(4);

        ASTReturn nodeReturn = new ASTReturn(JJTRETURN);

        Object res = nodeReturn.jjtAccept(interpreterVisitor, null);

        //Check return
        Assert.assertNull(res);

        //Check peek
        Assert.assertEquals(4, interpreterVisitor.getAddress());

    }

    @Test(expected = VisitorException.class)
    public void nodeReturnExceptionTest() throws VisitorException {

        ASTReturn nodeReturn = new ASTReturn(JJTRETURN);

        nodeReturn.jjtAccept(interpreterVisitor, null);
    }


    @Test
    public void nodeNewArray() throws TypeException, ArrayException, HeapException, StackException, VisitorException {
        ASTNewArray nodeNewArray = new ASTNewArray(JJTNEWARRAY);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        ASTJCType nodeType = new ASTJCType(JJTJCTYPE);
        nodeType.jjtSetValue("entier");
        nodeNewArray.jjtAddChild(ident, 0);
        nodeNewArray.jjtAddChild(nodeType, 1);

        push(42);

        nodeNewArray.jjtAccept(interpreterVisitor,null);
        Assert.assertEquals(42, memory.getArraySize("x0"));
    }

    @Test
    public void nodeNewArrayNegativeSize() throws TypeException, ArrayException, HeapException, StackException {
        ASTNewArray nodeNewArray = new ASTNewArray(JJTNEWARRAY);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue("x0");
        ASTJCType nodeType = new ASTJCType(JJTJCTYPE);
        nodeType.jjtSetValue("entier");
        nodeNewArray.jjtAddChild(ident, 0);
        nodeNewArray.jjtAddChild(nodeType, 1);

        push(-42);


        Assert.assertThrows(
                VisitorException.class,
                () -> nodeNewArray.jjtAccept(interpreterVisitor,null)
        );

    }

    private void declTab(String id, int size, String type) throws HeapException, TypeException, SymbolException, ArrayException, StackException, VisitorException {
        // Array
        ASTNewArray nodeNewArray = new ASTNewArray(JJTNEWARRAY);
        ASTJCIdent ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue(id);
        ASTJCType nodeType = new ASTJCType(JJTJCTYPE);
        nodeType.jjtSetValue(type);
        nodeNewArray.jjtAddChild(ident, 0);
        nodeNewArray.jjtAddChild(nodeType, 1);

        push(size);
        nodeNewArray.jjtAccept(interpreterVisitor,null);
        Assert.assertEquals(64, memory.getArraySize(id));
    }

    @Test
    public void nodeAstoreValidIndex() throws HeapException, TypeException, SymbolException, ArrayException, StackException, VisitorException {
        declTab("tab0",64,"entier");
        // Index
        push(12);
        // value
        push(42);
        ASTAStore nodeAstore = new ASTAStore(JJTASTORE);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAstore.jjtAddChild(ident2,0);

        Assert.assertEquals(0,memory.getValueT("tab0",12));
        nodeAstore.jjtAccept(interpreterVisitor,null);
        Assert.assertEquals(42,memory.getValueT("tab0",12));
    }

    @Test
    public void nodeAstoreNotValidIndex_exceptionThrown() throws TypeException, ArrayException, HeapException, StackException, SymbolException, VisitorException {
        declTab("tab0",64,"entier");

        // Index
        push(128);
        // Value
        push(12);
        ASTAStore nodeAstore = new ASTAStore(JJTASTORE);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAstore.jjtAddChild(ident2,0);

        Assert.assertEquals(0,memory.getValueT("tab0",12));
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeAstore.jjtAccept(interpreterVisitor,null)
        );
    }


    @Test
    public void nodeALoadValidIndex() throws HeapException, TypeException, SymbolException, ArrayException, StackException, VisitorException {
        declTab("tab0",64,"entier");

        // Index
        push(5);
        ASTALoad nodeAload = new ASTALoad(JJTALOAD);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAload.jjtAddChild(ident2,0);

        // test
        memory.assignValT("tab0",42,5);
        nodeAload.jjtAccept(interpreterVisitor,null);
        Assert.assertEquals(new Quad(Type.toString(Type.OMEGA),42,Type.INTEGER,Nature.CST),memory.getStack().peek());
    }

    @Test
    public void nodeALoadNotValidIndex_exceptionThrown() throws TypeException, ArrayException, HeapException, StackException, SymbolException, VisitorException {
        declTab("tab0",64,"entier");

        // Index
        push(128);
        ASTALoad nodeAload = new ASTALoad(JJTALOAD);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAload.jjtAddChild(ident2,0);

        // test
        Assert.assertEquals(0,memory.getValueT("tab0",12));
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeAload.jjtAccept(interpreterVisitor,null)
        );
    }


    @Test
    public void nodeAIncValidIndex() throws HeapException, TypeException, SymbolException, ArrayException, StackException, VisitorException {
        declTab("tab0",64,"entier");

        // Index
        push(5);
        // value
        push(10);
        ASTAInc nodeAInc = new ASTAInc(JJTAINC);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAInc.jjtAddChild(ident2,0);

        // test
        nodeAInc.jjtAccept(interpreterVisitor,null);
        System.out.println(memory.getValueT("tab0",5));
        Assert.assertEquals(10,memory.getValueT("tab0",5));
    }

    @Test
    public void nodeAIncNotValidIndex_exceptionThrown() throws TypeException, ArrayException, HeapException, StackException, SymbolException, VisitorException {
        declTab("tab0",64,"entier");

        // Index
        push(128);
        // value
        push(10);
        ASTAInc nodeAInc = new ASTAInc(JJTAINC);
        ASTJCIdent ident2 = new ASTJCIdent(JJTJCIDENT);
        ident2.jjtSetValue("tab0");

        nodeAInc.jjtAddChild(ident2,0);

        // test
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeAInc.jjtAccept(interpreterVisitor,null)
        );
    }


    @Ignore
    @Test
    public void nodeInfTest1() throws VisitorException, StackException, TypeException {

        push(0);
        push(8);
        push(7);

        ASTInf nodeInf = new ASTInf(JJTINF);

        //interpretation
        Object res = nodeInf.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(true, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //first operand == second operand

    @Test
    public void nodeInfTest2() throws VisitorException, StackException, TypeException {

        push(0);
        push(7);
        push(7);

        //-----------------

        //creation of jjc AST
        ASTInf nodeInf = new ASTInf(JJTINF);

        //interpretation
        Object res = nodeInf.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //first operand > second operand

    @Ignore
    @Test
    public void nodeInfTest3() throws VisitorException, StackException, TypeException {

        push(0);
        push(7);
        push(8);

        //-----------------

        //creation of jjc AST
        ASTInf nodeInf = new ASTInf(JJTINF);

        //interpretation
        Object res = nodeInf.jjtAccept(interpreterVisitor, null);

        //check return
        Assert.assertNull(res);

        //check adr
        Assert.assertEquals(2, interpreterVisitor.getAddress());

        //check stack
        Assert.assertEquals(false, memory.getStack().getFromTop(0).getValue());
        memory.getStack().dequeue();
        Assert.assertEquals(0, (int) memory.getStack().getFromTop(0).getValue());
    }
    //Missing value on stack

    @Test(expected = VisitorException.class)
    public void nodeInfExceptionTest() throws VisitorException, TypeException {

        push(7);

        //creation of jjc AST
        ASTInf nodeInf = new ASTInf(JJTINF);

        //interpretation
        nodeInf.jjtAccept(interpreterVisitor, null);

    }


    @Test
    public void nodeWriteLn() throws TypeException, VisitorException {
        ASTWriteln nodeWriteLN = new ASTWriteln(JJTWRITELN);
        memory.printLn("Hello");
        nodeWriteLN.jjtAccept(interpreterVisitor,null);
        Assert.assertTrue(interpreterVisitor.toString().contains("Hello\n"));
    }

    @Test
    public void nodeWriteLn_emptyStack(){
        ASTWriteln nodeWriteLN = new ASTWriteln(JJTWRITELN);
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeWriteLN.jjtAccept(interpreterVisitor,null)
        );
    }

    @Test
    public void nodeWrite() throws TypeException, VisitorException {
        ASTWrite nodeWrite = new ASTWrite(JJTWRITE);
        memory.printLn("Hello");
        nodeWrite.jjtAccept(interpreterVisitor,null);
        Assert.assertTrue(interpreterVisitor.toString().contains("Hello\n"));
    }

    @Test
    public void nodeWrite_emptyStack(){
        ASTWrite nodeWrite = new ASTWrite(JJTWRITE);
        Assert.assertThrows(
                VisitorException.class,
                () -> nodeWrite.jjtAccept(interpreterVisitor,null)
        );
    }

    @Test
    public void nodeSimpleNode(){
        SimpleNode simpleNode = new SimpleNode(0);
        Assert.assertThrows(
                VisitorException.class,
                () -> simpleNode.jjtAccept(interpreterVisitor,null)
        );
    }
}
