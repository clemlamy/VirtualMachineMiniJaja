package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class MemoryTest {
    public SymbolTable symbolTable;
    public Memory memory;

    @Before
    public void setup() {
        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);
    }

    @Test
    public void test_variableClass_emptyStack_thenExceptionThrown(){
        Assert.assertThrows(
                StackException.class,
                () -> memory.variableClass()
        );
    }

    @Test
    public void test_variableClass() throws TypeException, SymbolException, StackException {
        memory.declVar("Class1",Type.toString(Type.OMEGA),Type.OMEGA);
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declVar("var2", 12, Type.INTEGER);
        memory.declVar("var3", 0, Type.INTEGER);

        Assert.assertEquals("Class1",memory.variableClass());

    }

    @Test
    public void test_declVar_integer() throws TypeException, SymbolException, StackException {
        memory.declVar("id", 42, Type.INTEGER);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", 42, Type.INTEGER, Nature.VAR)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", 42, Type.INTEGER, Nature.VAR)
        );
    }

    @Test
    public void test_declVar_boolean() throws TypeException, SymbolException, StackException {
        memory.declVar("id", true, Type.BOOLEAN);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", true, Type.BOOLEAN, Nature.VAR)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", true, Type.BOOLEAN, Nature.VAR)
        );
    }

    @Test
    public void test_declCst_integerNotNull() throws TypeException, SymbolException, StackException {
        memory.declCst("id", 42, Type.INTEGER);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", 42, Type.INTEGER, Nature.CST)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", 42, Type.INTEGER, Nature.CST)
        );
    }

    @Test
    public void test_declCst_integerNull() throws TypeException, SymbolException, StackException {
        memory.declCst("id", null, Type.INTEGER);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", null, Type.INTEGER, Nature.VCST)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", null, Type.INTEGER, Nature.VCST)
        );
    }

    @Test
    public void test_declCst_booleanNotNull() throws TypeException, SymbolException, StackException {
        memory.declCst("id", true, Type.BOOLEAN);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", true, Type.BOOLEAN, Nature.CST)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", true, Type.BOOLEAN, Nature.CST)
        );
    }

    @Test
    public void test_declCst_booleanNull() throws TypeException, SymbolException, StackException {
        memory.declCst("id", null, Type.BOOLEAN);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad("id", null, Type.BOOLEAN, Nature.VCST)
        );
        Assert.assertEquals(
                memory.getSymbolTable().get("id"),
                new Quad("id", null, Type.BOOLEAN, Nature.VCST)
        );
    }

    @Test
    public void test_identVal_emptyStack_exceptionThrown() {
        Assert.assertThrows(
                StackException.class,
                () -> memory.identVal("id", Type.INTEGER,0)
        );
    }

    @Test
    public void test_identVal_tooHighIndex_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declVar("var2", 12, Type.INTEGER);
        memory.declVar("var3", 0, Type.INTEGER);
        Assert.assertThrows(
                IndexOutOfBoundsException.class,
                () -> memory.identVal("myVar",Type.INTEGER,42)
        );
    }

    @Test
    public void test_identVal_oneElement() throws TypeException, SymbolException, StackException {
        memory.declVar(Type.toString(Type.OMEGA), 42, Type.INTEGER);
        memory.identVal("id",Type.INTEGER,0);
        Assert.assertEquals(
                new Quad("id",42,Type.INTEGER,Nature.VAR),
                memory.getStack().peek()
        );
    }

    @Test
    public void test_identVal_severalElement() throws TypeException, SymbolException, StackException {
        memory.declVar("y", 42, Type.INTEGER);
        memory.declCst(Type.toString(Type.OMEGA), 12, Type.INTEGER);
        memory.declVar("x", 0, Type.INTEGER);
        memory.identVal("myVar",Type.INTEGER,1);
        Assert.assertEquals(
                new Quad("myVar",12,Type.INTEGER,Nature.VAR),
                memory.getStack().getFromTop(1)
        );
    }

    @Test
    public void test_identVal_oneElement_integerVar() throws TypeException, SymbolException, StackException {
        memory.declCst(Type.toString(Type.OMEGA), 42, Type.INTEGER);
        memory.identVal("id",Type.INTEGER,0);
        Assert.assertEquals(
                new Quad("id",42,Type.INTEGER,Nature.VAR),
                memory.getStack().peek()
        );
    }

    @Test
    public void test_print() throws TypeException, StackException {
        String needle = "Hello world !";
        memory.print(needle);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad(Type.toString(Type.OMEGA),needle,Type.OMEGA,Nature.VAR)
        );
        Assert.assertEquals(
                memory.getStack().peek().getValue().toString(),
                needle
        );
    }

    @Test
    public void test_println() throws TypeException, StackException {
        String needle = "Hello world !";
        memory.printLn(needle);
        Assert.assertEquals(
                memory.getStack().peek(),
                new Quad(Type.toString(Type.OMEGA),needle+"\n",Type.OMEGA,Nature.VAR)
        );
        Assert.assertEquals(
                memory.getStack().peek().getValue().toString(),
                needle+"\n"
        );
    }


    @Test
    public void test_removeDecl_empty_exceptionThrown() {
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.removeDecl("id")
        );
    }

    @Test
    public void test_removeDecl_oneElementDeclared_var() throws TypeException, SymbolException, StackException, HeapException {
        memory.declVar("id", 42, Type.INTEGER);
        Assert.assertEquals(
                new Quad("id", 42, Type.INTEGER, Nature.VAR),
                memory.removeDecl("id")
        );
        Assert.assertEquals(0,memory.getStack().getValues().size());
    }

    @Test
    public void test_removeDecl_oneElementDeclared_cst() throws TypeException, SymbolException, StackException, HeapException {
        memory.declCst("id", true, Type.BOOLEAN);
        Assert.assertEquals(
                new Quad("id", true, Type.BOOLEAN, Nature.CST),
                memory.removeDecl("id")
        );
        Assert.assertEquals(0,memory.getStack().getValues().size());
    }

    @Test
    public void test_removeDecl_oneElementDeclared_tab() throws TypeException, SymbolException, StackException, HeapException {
        memory.declTab("id", 42, Type.INTEGER);
        Assert.assertEquals(
                new Quad("id", 1, Type.INTEGER, Nature.TAB),
                memory.removeDecl("id")
        );
        Assert.assertEquals(0,memory.getStack().getValues().size());
    }

    @Test
    public void test_removeDecl_oneElementDeclared_vcst() throws TypeException, SymbolException, StackException, HeapException {
        memory.declCst("id", null, Type.INTEGER);
        Assert.assertEquals(
                new Quad("id", null, Type.INTEGER, Nature.VCST),
                memory.removeDecl("id")
        );
        Assert.assertEquals(0,memory.getStack().getValues().size());
    }

    @Test
    public void test_removeDecl_severalElementsDeclared() throws TypeException, SymbolException, StackException, HeapException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(5,memory.getStack().getValues().size());
        Assert.assertEquals(
                new Quad("var2", true, Type.BOOLEAN, Nature.VAR),
                memory.removeDecl("var2")
        );
        Assert.assertEquals(4,memory.getStack().getValues().size());
    }

    @Test
    public void test_removeDecl_unknownSymbol_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        Assert.assertEquals(2,memory.getStack().getValues().size());
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.removeDecl("unknown")
        );
        Assert.assertEquals(2,memory.getStack().getValues().size());
    }

    @Test
    public void test_getValue_unknownSymbol_exceptionThrown() {
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getValue("id")
        );
    }

    @Test
    public void test_getValue_unknownSymbol2_exceptionThrown() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getValue("id")
        );
    }

    @Test
    public void test_getValue_vcst() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertThrows(
                SymbolException.class,
                () -> Assert.assertNull(memory.getValue("cst1"))
        );
    }

    @Test
    public void test_getValue_cst() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(51,memory.getValue("cst2"));
    }

    @Test
    public void test_getValue_var() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(42,memory.getValue("var1"));
    }

    @Ignore("release 2")
    @Test
    public void test_getValueMethode() {
        // TODO (release 2)
    }


    @Test
    public void test_getNature_unknownSymbol_exceptionThrown() {
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getNature("id")
        );
    }

    @Test
    public void test_getNature_unknownSymbol2_exceptionThrown() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getNature("id")
        );
    }

    @Test
    public void test_getNature_vcst() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(
                Nature.VCST,
                memory.getNature("cst1")
        );
    }

    @Test
    public void test_getNature_cst() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(
                Nature.CST,
                memory.getNature("cst2")
        );
    }

    @Test
    public void test_getNature_var() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(
                Nature.VAR,
                memory.getNature("var2")
        );
    }

    @Test
    public void test_getNature_methode() throws SymbolException, TypeException {
        memory.declMethode("var1", 42, Type.INTEGER);
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declMethode("fct1", null, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        memory.declCst("cst2", 51, Type.INTEGER);
        memory.declVar("var3", false, Type.BOOLEAN);
        Assert.assertEquals(
                Nature.METH,
                memory.getNature("fct1")
        );
    }

    @Test
    public void test_getNature_array() throws HeapException, TypeException, SymbolException {
        memory.declTab("var1", 42, Type.INTEGER);
        Assert.assertEquals(
                Nature.TAB,
                memory.getNature("var1")
        );
    }

    @Test
    public void test_getType_unknownSymbol_exceptionThrown(){
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getType("id")
        );
    }
    @Test
    public void test_getType_unknownSymbol2_exceptionThrown() throws SymbolException, TypeException {
        memory.declCst("cst1", null, Type.INTEGER);
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.getType("id")
        );
    }


    @Test
    public void test_getType_vcst() throws TypeException, SymbolException {
        memory.declCst("cst1", null, Type.INTEGER);
        memory.declCst("cst2", null, Type.BOOLEAN);
        Assert.assertEquals(
                Type.INTEGER,
                memory.getType("cst1")
        );
        Assert.assertEquals(
                Type.BOOLEAN,
                memory.getType("cst2")
        );
    }

    @Test
    public void test_getType_cst() throws TypeException, SymbolException {
        memory.declCst("cst1", 42, Type.INTEGER);
        memory.declCst("cst2", true, Type.BOOLEAN);
        Assert.assertEquals(
                Type.INTEGER,
                memory.getType("cst1")
        );
        Assert.assertEquals(
                Type.BOOLEAN,
                memory.getType("cst2")
        );
    }

    @Test
    public void test_getType_var() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.declVar("var2", true, Type.BOOLEAN);
        Assert.assertEquals(
                Type.INTEGER,
                memory.getType("var1")
        );
        Assert.assertEquals(
                Type.BOOLEAN,
                memory.getType("var2")
        );
    }

    @Test
    public void test_getType_methode() throws SymbolException, TypeException {
        memory.declMethode("fct1", null, Type.INTEGER);
        memory.declMethode("fct2", null, Type.BOOLEAN);
        memory.declMethode("fct3", null, Type.VOID);
        Assert.assertEquals(
                Type.INTEGER,
                memory.getType("fct1")
        );
        Assert.assertEquals(
                Type.BOOLEAN,
                memory.getType("fct2")
        );
        Assert.assertEquals(
                Type.VOID,
                memory.getType("fct3")
        );
    }

    @Test
    public void test_getType_array() throws HeapException, TypeException, SymbolException {
        memory.declTab("tab1", 42, Type.INTEGER);
        Assert.assertEquals(
                Type.INTEGER,
                memory.getType("tab1")
        );
    }

    // AssignValT
    @Test
    public void test_assignValTUnknownSymbol_exceptionThrown() throws TypeException, SymbolException, HeapException {
        memory.declTab("tab1", 42, Type.INTEGER);
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.assignValT("id",24,0)
        );
    }

    @Test
    public void test_assignValTInvalidType_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                ArrayException.class,
                () -> memory.assignValT("var1",true,0)
        );
    }

    @Test
    public void test_assignValTGetValTNoneArrayElement_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                ArrayException.class,
                () -> memory.assignValT("var1",true,0)
        );
        Assert.assertThrows(
                ArrayException.class,
                () -> memory.getValueT("var1",2)
        );
    }

    @Test
    public void test_assignValTGetValT() throws TypeException, SymbolException, HeapException, ArrayException, StackException {
        memory.declTab("tab1", 42, Type.INTEGER);
        memory.assignValT("tab1",123484,3);
        Assert.assertEquals(123484, memory.getValueT("tab1",3));
    }

    // AssignVal
    @Test
    public void test_assignValUnknownSymbol_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                StackException.class,
                () -> memory.assignVal("id",24)
        );
    }

    @Test
    public void test_assignValInvalidType_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                TypeException.class,
                () -> memory.assignVal("var1",true)
        );
    }

    @Test
    public void test_assignVal_alreadyDefinedConst_exceptionThrown() throws SymbolException, TypeException {
        memory.declCst("cst1", 42, Type.INTEGER);
        Assert.assertThrows(
                ConstantException.class,
                () -> memory.assignVal("cst1",24)
        );
    }

    @Test
    public void test_assignVal_notDefinedConst() throws StackException, SymbolException, ConstantException, TypeException, HeapException {
        memory.declCst("cst1", null, Type.INTEGER);
        Assert.assertEquals(1,memory.getStack().getValues().size());
        Quad q = memory.assignVal("cst1",42);
        Assert.assertEquals(1,memory.getStack().getValues().size());
        Assert.assertEquals(
                42,
                memory.getValue("cst1")
        );
        Assert.assertEquals(
                Nature.CST,
                memory.getNature("cst1")
        );
        Assert.assertEquals(
                new Quad("cst1",42,Type.INTEGER,Nature.CST),
                q
        );
    }

    @Test
    public void test_assignVal_nullValue_exceptionThrown() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertEquals(1,memory.getStack().getValues().size());
        Assert.assertThrows(
                TypeException.class,
                () -> memory.assignVal("var1",null)
        );
    }

    @Test
    public void test_assignVal_var() throws StackException, SymbolException, ConstantException, TypeException, HeapException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertEquals(1,memory.getStack().getValues().size());
        Quad q = memory.assignVal("var1",24);
        Assert.assertEquals(1,memory.getStack().getValues().size());
        Assert.assertEquals(
                24,
                memory.getValue("var1")
        );
        Assert.assertEquals(
                new Quad("var1",24,Type.INTEGER,Nature.VAR),
                q
        );
    }

    @Test
    public void test_getArrayLength() throws HeapException, TypeException, SymbolException, ArrayException, StackException {
        memory.declTab("tab1", 42, Type.INTEGER);
        Assert.assertEquals(
                42,
                memory.getArraySize("tab1")
        );
    }

    @Test
    public void test_getArrayLengthEmptyStack_exceptionThrown(){
        Assert.assertThrows(
                StackException.class,
                () -> memory.getArraySize("tab1")
        );
    }

    @Test
    public void test_getArrayLengthUnknownSymbol_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                StackException.class,
                () -> memory.getArraySize("tab1")
        );
    }

    @Test
    public void test_getArrayLengthNotArrayElement_exceptionThrown() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                ArrayException.class,
                () -> memory.getArraySize("var1")
        );
    }

    @Test
    public void test_setType() throws SymbolException, TypeException {
        memory.declVar("var1", 42, Type.INTEGER);
        memory.setType("var1",Type.BOOLEAN);
        Assert.assertEquals(Type.BOOLEAN, memory.getType("var1"));
    }

    @Test
    public void test_setTypeUnknownSymbol() throws TypeException, SymbolException {
        memory.declVar("var1", 42, Type.INTEGER);
        Assert.assertThrows(
                SymbolException.class,
                () -> memory.setType("prout",Type.BOOLEAN)
        );
    }
}
