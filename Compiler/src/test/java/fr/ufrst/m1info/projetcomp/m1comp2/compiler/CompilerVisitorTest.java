package fr.ufrst.m1info.projetcomp.m1comp2.compiler;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.SimpleNode;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.JajaCodeTreeConstants.*;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.JJTSUP;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.*;


public class CompilerVisitorTest {

    private CompilerVisitor compilerVisitor;
    private ArrayList<Node> listInstr;

    private ASTident makeIdent(String ident) {
        var nodeIdent = new ASTident(JJTIDENT);
        nodeIdent.jjtSetValue(ident);
        return nodeIdent;
    }

    private ASTnbre makeNumber(int val) {
        var nodeNbre = new ASTnbre(JJTNBRE);
        nodeNbre.jjtSetValue(val);
        return nodeNbre;
    }

    private ASTvar makeVar(String varName, Integer val) {
        var nodeVar = new ASTvar(JJTVAR);
        var nodeValue = val == null
                ? new ASTomega(JJTOMEGA)
                : makeNumber(val);

        nodeVar.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeVar.jjtAddChild(makeIdent(varName), 1);
        nodeVar.jjtAddChild(nodeValue, 2);

        return nodeVar;
    }

    private ASTvar makeVar(String varName, Boolean val) {
        var nodeVar = new ASTvar(JJTVAR);
        SimpleNode nodeValue = new ASTomega(JJTOMEGA);

        if (val != null) {
            if (val)
                nodeValue = new ASTvrai(JJTVRAI);
            else
                nodeValue = new ASTfaux(JJTFAUX);
        }


        nodeVar.jjtAddChild(new ASTbooleen(JJTBOOLEEN), 0);
        nodeVar.jjtAddChild(makeIdent(varName), 1);
        nodeVar.jjtAddChild(nodeValue, 2);

        return nodeVar;
    }

    @Before
    public void init() {
        compilerVisitor = new CompilerVisitor();
        listInstr = new ArrayList<>();
    }

    @Test
    public void nodeTrueTest() {
        var nodeTrue = new ASTvrai(JJTVRAI);

        var n = (int) compilerVisitor.visit(nodeTrue, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(1, n);

        listInstr = compilerVisitor.getListInstr();

        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(0).jjtGetChild(0) instanceof ASTJCVrai);

    }


    @Test
    public void nodeFalseTest() {
        var nodeFalse = new ASTfaux(JJTFAUX);

        var n = (int) compilerVisitor.visit(nodeFalse, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(1, n);

        listInstr = compilerVisitor.getListInstr();

        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(0).jjtGetChild(0) instanceof ASTJCFaux);
    }

    @Test
    public void nodeEmptyClasseTest() throws VisitorException {
        // Building AST
        var nodeMain = new ASTmain(JJTMAIN);
        nodeMain.jjtAddChild(new ASTvnil(JJTVNIL), 0);
        nodeMain.jjtAddChild(new ASTinil(JJTINIL), 1);

        var nodeClass = new ASTclasse(JJTMAIN);
        var nodeIdent = makeIdent("C");

        nodeClass.jjtAddChild(nodeIdent, 0);
        nodeClass.jjtAddChild(new ASTvnil(JJTVNIL), 1);
        nodeClass.jjtAddChild(nodeMain, 2);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeClass, new CompilerData(1, CompilerMode.DEFAULT, null));

        listInstr = compilerVisitor.getListInstr();

        Assert.assertEquals(4, n);
        Assert.assertTrue(listInstr.get(0) instanceof ASTInit);
        Assert.assertTrue(listInstr.get(1) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(2) instanceof ASTPop);
        Assert.assertTrue(listInstr.get(3) instanceof ASTJCStop);
    }

    @Test
    public void nodeClasseTest() throws VisitorException {
        var nodeDecls1 = new ASTdecls(JJTDECLS);
        var nodeDecls2 = new ASTdecls(JJTDECLS);

        nodeDecls1.jjtAddChild(makeVar("a@global", false), 0);
        nodeDecls1.jjtAddChild(nodeDecls2, 1);

        nodeDecls2.jjtAddChild(makeVar("y@global", 4), 0);
        nodeDecls2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var nodeVars1 = new ASTvars(JJTVARS);
        var nodeVars2 = new ASTvars(JJTVARS);

        nodeVars1.jjtAddChild(makeVar("y@main", true), 0);
        nodeVars1.jjtAddChild(nodeVars2, 1);

        nodeVars2.jjtAddChild(makeVar("x@main", false), 0);
        nodeVars2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var nodeMain = new ASTmain(JJTMAIN);

        nodeMain.jjtAddChild(nodeVars1, 0);
        nodeMain.jjtAddChild(new ASTinil(JJTINIL), 1);

        var nodeClass = new ASTclasse(JJTCLASSE);

        nodeClass.jjtAddChild(makeIdent("C"), 0);
        nodeClass.jjtAddChild(nodeDecls1, 1);
        nodeClass.jjtAddChild(nodeMain, 2);

        //Compilation

        var n = (int) compilerVisitor.visit(nodeClass, new CompilerData(1, CompilerMode.DEFAULT, null));

        Assert.assertEquals(20, n);

        //Expected instructions

        listInstr = compilerVisitor.getListInstr();
    }

    @Test
    public void nodeIdentTest() {
        var nodeIdent = makeIdent("ident");

        var n = (int) compilerVisitor.visit(nodeIdent, null);

        Assert.assertEquals(1, n);

        listInstr = compilerVisitor.getListInstr();

        Assert.assertTrue(listInstr.get(0) instanceof ASTLoad);
    }

    @Test
    public void nodeDeclsTest() throws VisitorException {
        var nodeDecls1 = new ASTdecls(JJTDECLS);
        var nodeDecls2 = new ASTdecls(JJTDECLS);

        var nodeVar1 = makeVar("a@global", (Boolean) null);

        nodeDecls1.jjtAddChild(nodeVar1, 0);
        nodeDecls1.jjtAddChild(nodeDecls2, 1);

        var nodeVar2 = makeVar("b@global", 7);

        nodeDecls2.jjtAddChild(nodeVar2, 0);
        nodeDecls2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var n1 = (int) compilerVisitor.visit(nodeDecls1, new CompilerData(1, CompilerMode.DEFAULT));
        var n2 = (int) compilerVisitor.visit(nodeDecls1, new CompilerData(n1, CompilerMode.REMOVE));

        ////System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(4, n1);
        Assert.assertEquals(4, n2);

    }

    @Test
    public void nodeVnilTest() {
        var nodeVnil = new ASTvnil(JJTVNIL);

        var n = (int) compilerVisitor.visit(nodeVnil, null);

        Assert.assertEquals(0, n);
        Assert.assertTrue(listInstr.isEmpty());
    }

    @Test
    public void nodeCstTest() throws VisitorException {
        // MJJ AST
        var nodeCst = new ASTcst(JJTCST);

        nodeCst.jjtAddChild(new ASTbooleen(JJTBOOLEEN), 0);
        nodeCst.jjtAddChild(makeIdent("x@main"), 1);
        nodeCst.jjtAddChild(new ASTfaux(JJTFAUX), 2);

        // Compilation
        var n1 = (int) compilerVisitor.visit(nodeCst, new CompilerData(1, CompilerMode.DEFAULT));
        var n2 = (int) compilerVisitor.visit(nodeCst, new CompilerData(n1, CompilerMode.REMOVE));

        Assert.assertEquals(2, n1);
        Assert.assertEquals(2, n2);

        // Expected instrs
    }

    @Test
    public void nodeArrayTest() throws VisitorException {
        // MJJ AST
        var nodeArray = new ASTtableau(JJTTABLEAU);

        nodeArray.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeArray.jjtAddChild(makeIdent("tab@global"), 1);
        nodeArray.jjtAddChild(makeNumber(4), 2);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeArray, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);
        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTNewArray);
    }

    @Test
    public void nodeMethodTest() throws VisitorException {
        // Construct MJJ AST
        // -------------- headers -----------------
        var nodeHeaders1 = new ASTentetes(JJTENTETES);
        var nodeHeaders2 = new ASTentetes(JJTENTETES);

        var nodeHeader1 = new ASTentete(JJTENTETE);
        var nodeHeader2 = new ASTentete(JJTENTETE);

        nodeHeader1.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeHeader1.jjtAddChild(makeIdent("arg1@fct:INTEGER,BOOLEAN"), 1);

        nodeHeader2.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeHeader2.jjtAddChild(makeIdent("arg2@fct:INTEGER,BOOLEAN"), 1);

        nodeHeaders1.jjtAddChild(nodeHeader1, 0);
        nodeHeaders1.jjtAddChild(nodeHeader2, 1);
        nodeHeaders2.jjtAddChild(nodeHeader2, 0);
        nodeHeaders2.jjtAddChild(new ASTenil(JJTENIL), 1);

        //---------------- vars -----------------
        var nodeVars1 = new ASTvars(JJTVARS);
        var nodeVar = makeVar("localVar@fct:INTEGER,BOOLEAN", 10);
        nodeVars1.jjtAddChild(nodeVar, 0);
        nodeVars1.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        //--------------- instrs ------------------
        var nodeAssignment = new ASTaffectation(JJTAFFECTATION);
        nodeAssignment.jjtAddChild(makeIdent("localVar@fct:INTEGER,BOOLEAN"), 0);
        var nodeMult = new ASTmult(JJTMUL);
        nodeMult.jjtAddChild(makeIdent("localVar@fct:INTEGER,BOOLEAN"), 0);
        nodeMult.jjtAddChild(makeIdent("arg1@fct:INTEGER,BOOLEAN"), 1);
        nodeAssignment.jjtAddChild(nodeMult, 1);

        var nodeReturn = new ASTretour(JJTRETOUR);
        var nodeAdd = new ASTplus(JJTADD);
        nodeAdd.jjtAddChild(makeIdent("localVar@fct:INTEGER,BOOLEAN"), 0);
        nodeAdd.jjtAddChild(makeIdent("arg2@fct:INTEGER,BOOLEAN"), 1);
        nodeReturn.jjtAddChild(nodeMult, 0);

        var nodeInstrs1 = new ASTinstrs(JJTINSTRS);
        var nodeInstrs2 = new ASTinstrs(JJTINSTRS);
        nodeInstrs1.jjtAddChild(nodeAssignment, 0);
        nodeInstrs1.jjtAddChild(nodeInstrs2, 1);
        nodeInstrs2.jjtAddChild(nodeReturn, 0);
        nodeInstrs2.jjtAddChild(new ASTinil(JJTINIL), 1);

        //------------------ method ----------------------
        var nodeMethod = new ASTmethode(JJTMETHODE);

        nodeMethod.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeMethod.jjtAddChild(makeIdent("fct@global:INTEGER,BOOLEAN"), 1);
        nodeMethod.jjtAddChild(nodeHeaders1, 2);
        nodeMethod.jjtAddChild(nodeVars1, 3);
        nodeMethod.jjtAddChild(nodeInstrs1, 4);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeMethod, new CompilerData(1, CompilerMode.DEFAULT));

        ////System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(18, n);

        // Expected instrs
    }

    @Test
    public void nodeVarTest() throws VisitorException {
        // AST MJJ
        var nodeVar = makeVar("x@main", false);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeVar, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);
    }

    @Test
    public void nodeVarRemoveTest() throws VisitorException {
        // AST MJJ
        var nodeVar = makeVar("x@main", false);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeVar, new CompilerData(1, CompilerMode.REMOVE));

        Assert.assertEquals(2, n);
        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTSwap);
        Assert.assertTrue(listInstr.get(1) instanceof ASTPop);
    }

    @Test
    public void nodeVarsTest() throws VisitorException {
        var nodeVars1 = new ASTvars(JJTVARS);
        var nodeVars2 = new ASTvars(JJTVARS);

        var nodeVar1 = makeVar("y@main", 42);
        var nodeVar2 = makeVar("x@main", 24);

        nodeVars1.jjtAddChild(nodeVar1, 0);
        nodeVars1.jjtAddChild(nodeVars2, 1);

        nodeVars2.jjtAddChild(nodeVar2, 0);
        nodeVars2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        // Compilation
        var n1 = (int) compilerVisitor.visit(nodeVars1, new CompilerData(1, CompilerMode.DEFAULT));
        var n2 = (int) compilerVisitor.visit(nodeVars1, new CompilerData(n1, CompilerMode.REMOVE));

        ////System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));

        // Expected instrs
        Assert.assertEquals(4, n1);
        Assert.assertEquals(4, n2);
    }

    @Test
    public void nodeMainTest() throws VisitorException {
        // AST MJJ
        var nodeVars1 = new ASTvars(JJTVARS);
        var nodeVars2 = new ASTvars(JJTVARS);

        var nodeVar1 = makeVar("y@main", 42);
        var nodeVar2 = makeVar("x@main", 24);

        nodeVars1.jjtAddChild(nodeVar1, 0);
        nodeVars1.jjtAddChild(nodeVars2, 1);

        nodeVars2.jjtAddChild(nodeVar2, 0);
        nodeVars2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var nodeMain = new ASTmain(JJTMAIN);

        nodeMain.jjtAddChild(nodeVars1, 0);
        nodeMain.jjtAddChild(new ASTinil(JJTINIL), 1);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeMain, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(9, n);

        // Expected instrs
    }

    @Test
    public void nodeMainEmptyTest() throws VisitorException {
        // AST MJJ
        var nodeMain = new ASTmain(JJTMAIN);

        nodeMain.jjtAddChild(new ASTvnil(JJTVNIL), 0);
        nodeMain.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeMain, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(1, n);
        listInstr = compilerVisitor.getListInstr();
        // Expected instrs
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
    }

    @Test
    public void nodeHeadersTest() throws VisitorException {
        // AST MJJ
        var nodeHeaders1 = new ASTentetes(JJTENTETES);
        var nodeHeaders2 = new ASTentetes(JJTENTETES);

        var nodeHeader1 = new ASTentete(JJTENTETE);
        var nodeHeader2 = new ASTentete(JJTENTETE);

        nodeHeader1.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeHeader1.jjtAddChild(makeIdent("arg1@fct:INTEGER,BOOLEAN"), 1);

        nodeHeader2.jjtAddChild(new ASTbooleen(JJTBOOLEEN), 0);
        nodeHeader2.jjtAddChild(makeIdent("arg2@fct:INTEGER,BOOLEAN"), 1);

        nodeHeaders1.jjtAddChild(nodeHeader1, 0);
        nodeHeaders1.jjtAddChild(nodeHeaders2, 1);
        nodeHeaders2.jjtAddChild(nodeHeader2, 0);
        nodeHeaders2.jjtAddChild(new ASTenil(JJTENIL), 1);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeHeaders1, new CompilerData(1, CompilerMode.DEFAULT, 2));

        Assert.assertEquals(2, n);

        // Expected instrs
    }

    @Test
    public void nodeHeaderTest() {
        // AST MJJ
        var nodeHeader = new ASTentete(JJTENTETE);

        nodeHeader.jjtAddChild(new ASTentier(JJTENTIER), 0);
        nodeHeader.jjtAddChild(makeIdent("arg@fct:INTEGER,BOOLEAN"), 1);

        //Compilation
        var n = (int) compilerVisitor.visit(nodeHeader, new CompilerData(1, CompilerMode.DEFAULT, 3));

        Assert.assertEquals(1, n);

        //Expected instrs
    }

    @Test
    public void nodeEnilTest() {
        var nodeEnil = new ASTenil(JJTENIL);

        var n = (int) compilerVisitor.visit(nodeEnil, null);

        Assert.assertEquals(0, n);
        Assert.assertTrue(listInstr.isEmpty());
    }

    @Test
    public void nodeInstrsTest() throws VisitorException {
        var nodeInstrs1 = new ASTinstrs(JJTINSTRS);
        var nodeInstrs2 = new ASTinstrs(JJTINSTRS);

        var nodeReturn = new ASTretour(JJTRETOUR);
        nodeReturn.jjtAddChild(new ASTvrai(JJTVRAI), 0);

        var nodeAssignment1 = new ASTaffectation(JJTAFFECTATION);
        nodeAssignment1.jjtAddChild(makeIdent("x@global"), 0);
        nodeAssignment1.jjtAddChild(makeNumber(1), 1);

        nodeInstrs1.jjtAddChild(nodeReturn, 0);
        nodeInstrs1.jjtAddChild(nodeInstrs2, 1);

        nodeInstrs2.jjtAddChild(nodeAssignment1, 0);
        nodeInstrs2.jjtAddChild(new ASTinil(JJTINIL), 1);

        var n = (int) compilerVisitor.visit(nodeInstrs1, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(3, n);

        // Expected instrs
    }

    @Test
    public void nodeInilTest() {
        var nodeInil = new ASTinil(JJTINIL);

        var n = (int) compilerVisitor.visit(nodeInil, new CompilerData(1, CompilerMode.DEFAULT));
        Assert.assertEquals(0, n);
    }

    @Test
    public void nodeReturnTest() throws VisitorException {
        var nodeReturn = new ASTretour(JJTRETOUR);
        nodeReturn.jjtAddChild(makeNumber(4), 0);

        var n = (int) compilerVisitor.visit(nodeReturn, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(1, n);

        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
    }

    @Test
    public void nodeWriteTest() throws VisitorException {
        var nodeWrite = new ASTecrire(JJTWRITE);
        var nodeString = new ASTchaine(JJTCHAINE);
        nodeString.jjtSetValue("Hello World!");

        nodeWrite.jjtAddChild(nodeString, 0);

        var n = (int) compilerVisitor.visit(nodeWrite, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);

        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTWrite);
    }

    @Test
    public void nodeWritelnTest() throws VisitorException {
        var nodeWriteln = new ASTecrireln(JJTWRITELN);
        var nodeString = new ASTchaine(JJTCHAINE);
        nodeString.jjtSetValue("Hello World!");

        nodeWriteln.jjtAddChild(nodeString, 0);

        var n = (int) compilerVisitor.visit(nodeWriteln, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);

        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTWriteln);
    }

    @Test
    public void nodeIfTest() throws VisitorException {
        var nodeIf = new ASTsi(JJTSI);
        var nodeInstr1 = new ASTinstrs(JJTINSTRS);
        var nodeInstr2 = new ASTinstrs(JJTINSTRS);
        var nodeAssignment1 = new ASTaffectation(JJTAFFECTATION);
        var nodeAssignment2 = new ASTaffectation(JJTAFFECTATION);

        nodeIf.jjtAddChild(new ASTvrai(JJTVRAI), 0);
        nodeIf.jjtAddChild(nodeInstr1, 1);
        nodeIf.jjtAddChild(nodeInstr2, 2);

        nodeInstr1.jjtAddChild(nodeAssignment1, 0);
        nodeInstr1.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeInstr2.jjtAddChild(nodeAssignment2, 0);
        nodeInstr2.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeAssignment1.jjtAddChild(makeIdent("x@global"), 0);
        nodeAssignment1.jjtAddChild(makeNumber(42), 1);

        nodeAssignment2.jjtAddChild(makeIdent("y@global"), 0);
        nodeAssignment2.jjtAddChild(makeNumber(24), 1);

        var n = (int) compilerVisitor.visit(nodeIf, new CompilerData(1, CompilerMode.DEFAULT));


        Assert.assertEquals(7, n);
        listInstr = compilerVisitor.getListInstr();

        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTIf);
        Assert.assertTrue(listInstr.get(2) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(3) instanceof ASTStore);
        Assert.assertTrue(listInstr.get(4) instanceof ASTGoto);
        Assert.assertTrue(listInstr.get(5) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(6) instanceof ASTStore);
    }

    @Test
    public void nodeWhileTest() throws VisitorException {
        var nodeWhile = new ASTtantque(JJTTANTQUE);
        var nodeInstrs = new ASTinstrs(JJTINSTRS);
        var nodeAssignment = new ASTaffectation(JJTAFFECTATION);

        nodeWhile.jjtAddChild(new ASTvrai(JJTVRAI), 0);
        nodeWhile.jjtAddChild(nodeInstrs, 1);

        nodeInstrs.jjtAddChild(nodeAssignment, 0);
        nodeInstrs.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeAssignment.jjtAddChild(makeIdent("x@global"), 0);
        nodeAssignment.jjtAddChild(makeNumber(1), 1);

        var n = (int) compilerVisitor.visit(nodeWhile, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(6, n);
        listInstr = compilerVisitor.getListInstr();
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTNot);
        Assert.assertTrue(listInstr.get(2) instanceof ASTIf);
        Assert.assertTrue(listInstr.get(3) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(4) instanceof ASTStore);
        Assert.assertTrue(listInstr.get(5) instanceof ASTGoto);


    }



    @Test
    public void nodeCallITest() throws VisitorException {
        var nodeCallI = new ASTappelI(JJTAPPELI);
        var nodeListExp1 = new ASTlistexp(JJTLISTEXP);
        var nodeListExp2 = new ASTlistexp(JJTLISTEXP);

        nodeListExp1.jjtAddChild(makeNumber(4), 0);
        nodeListExp1.jjtAddChild(nodeListExp2, 1);
        nodeListExp2.jjtAddChild(new ASTfaux(JJTFAUX), 0);
        nodeListExp2.jjtAddChild(new ASTenil(JJTENIL), 1);

        nodeCallI.jjtAddChild(makeIdent("fct@fct:INTEGER,BOOLEAN"), 0);
        nodeCallI.jjtAddChild(nodeListExp1, 1);

        var n = (int) compilerVisitor.visit(nodeCallI, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(8, n);

        listInstr = compilerVisitor.getListInstr();

        ////System.out.println(CompilerToString.instrToString(listInstr));
        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(2) instanceof ASTInvoke);
        Assert.assertTrue(listInstr.get(3) instanceof ASTSwap);
        Assert.assertTrue(listInstr.get(4) instanceof ASTPop);
        Assert.assertTrue(listInstr.get(5) instanceof ASTSwap);
        Assert.assertTrue(listInstr.get(6) instanceof ASTPop);
        Assert.assertTrue(listInstr.get(7) instanceof ASTPop);
    }

    @Test
    public void nodePlusTest() throws VisitorException {
        ASTplus plus = new ASTplus(JJTPLUS);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(2);
        plus.jjtAddChild(nbre, 0);
        plus.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(plus, new CompilerData(1, CompilerMode.DEFAULT));

        //check return
        Assert.assertEquals(3, n);
    }

    @Test
    public void nodeMoinsTest() throws VisitorException {
        ASTmoins moins = new ASTmoins(JJTMOINS);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(3);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(1);
        moins.jjtAddChild(nbre, 0);
        moins.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(moins, new CompilerData(1, CompilerMode.DEFAULT));

        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(3, n);
    }

    @Test
    public void nodeMultTest() throws VisitorException {
        ASTmult mult = new ASTmult(JJTMULT);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(2);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(3);
        mult.jjtAddChild(nbre, 0);
        mult.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(mult, new CompilerData(1, CompilerMode.DEFAULT));
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        //check return
        Assert.assertEquals(3, n);
    }

    @Test
    public void nodeDivTest() throws VisitorException {
        ASTdiv div = new ASTdiv(MiniJajaTreeConstants.JJTDIV);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(6);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(3);
        div.jjtAddChild(nbre, 0);
        div.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(div, new CompilerData(1, CompilerMode.DEFAULT));
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));

        Assert.assertEquals(3, n);
    }

    @Test
    public void nodeNotTest() throws VisitorException {
        ASTnon not = new ASTnon(JJTNON);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        not.jjtAddChild(vrai, 0);

        var n = (int) compilerVisitor.visit(not, new CompilerData(1, CompilerMode.DEFAULT));

        //check return
        Assert.assertEquals(2, n);
    }


    @Test
    public void nodeAffectationTest() throws VisitorException {
        var affect = new ASTaffectation(JJTAFFECTATION);
        var ident = makeIdent("i");
        var nbre = makeNumber(3);

        affect.jjtAddChild(ident, 0);
        affect.jjtAddChild(nbre, 1);

        var n = (int) compilerVisitor.visit(affect, new CompilerData(1, CompilerMode.DEFAULT));
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(2, n);
    }

    @Test
    public void nodeSommeTest() throws VisitorException {
        var somme = new ASTsomme(JJTSOMME);
        var ident = makeIdent("i");
        var nbre = makeNumber(3);

        somme.jjtAddChild(ident, 0);
        somme.jjtAddChild(nbre, 1);

        var n = (int) compilerVisitor.visit(somme, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);
    }
    @Test
    public void nodeSommeTTest() throws VisitorException {
        //decl tableau1
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = makeIdent("t");
        ASTnbre nbre = makeNumber(1);

        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = makeNumber(0);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(index, 1);

        ASTnbre value = makeNumber(3);
        ASTnbre value2 = makeNumber(5);

        ASTaffectation affectation = new ASTaffectation(JJTAFFECTATION);
        affectation.jjtAddChild(tab, 0);
        affectation.jjtAddChild(value, 1);

        ASTsomme sommet = new ASTsomme(JJTSOMME);
        sommet.jjtAddChild(tab,0);
        sommet.jjtAddChild(value2,1);

        var n = (int) compilerVisitor.visit(sommet, new CompilerData(1, CompilerMode.DEFAULT));
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(3, n);
    }

    @Test
    public void nodeIncrementTest() throws VisitorException {
        var increment = new ASTincrement(JJTINCREMENT);
        var ident = makeIdent("i");
        increment.jjtAddChild(ident, 0);

        var n = (int) compilerVisitor.visit(increment, new CompilerData(1, CompilerMode.DEFAULT));
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(2, n);

    }

    @Test
    public void nodeIncrementTTest() throws VisitorException {
        //decl tableau1
        var tableau = new ASTtableau(JJTTABLEAU);
        var entier = new ASTentier(JJTENTIER);
        var ident = makeIdent("t");
        var nbre = makeNumber(1);

        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = makeNumber(0);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(index, 1);

        ASTnbre value = makeNumber(3);

        ASTaffectation affectation = new ASTaffectation(JJTAFFECTATION);
        affectation.jjtAddChild(tab, 0);
        affectation.jjtAddChild(value, 1);

        ASTincrement increment = new ASTincrement(JJTINCREMENT);
        increment.jjtAddChild(tab,0);

        var nodeClass = new ASTclasse(JJTCLASSE);
        nodeClass.jjtAddChild(makeIdent("C"), 0);

        var nodeDecls = new ASTdecls(JJTDECLS);
        nodeDecls.jjtAddChild(tableau, 0);
        nodeDecls.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        nodeClass.jjtAddChild(nodeDecls, 1);

        var nodeMain = new ASTmain(JJTMAIN);
        nodeClass.jjtAddChild(nodeMain, 2);

        nodeMain.jjtAddChild(new ASTvnil(JJTVNIL), 0);
        var nodeInstrs = new ASTinstrs(JJTINSTRS);
        nodeMain.jjtAddChild(nodeInstrs, 1);

        nodeInstrs.jjtAddChild(affectation, 0);
        nodeInstrs.jjtAddChild(new ASTinil(JJTINIL), 1);

        // Compilation
        var n = (int) compilerVisitor.visit(nodeClass, new CompilerData(1, CompilerMode.DEFAULT));


        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(11, n);

    }

    @Test
    public void nodeExnilTest() {
        ASTexnil exnil = new ASTexnil(JJTEXNIL);

        var n = (int) compilerVisitor.visit(exnil, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(0, n);

    }

    @Test
    public void nodeNegTest() throws VisitorException {
        ASTneg neg = new ASTneg(MiniJajaTreeConstants.JJTNEG);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        neg.jjtAddChild(nbre, 0);

        var n = (int) compilerVisitor.visit(neg, new CompilerData(1, CompilerMode.DEFAULT));

        Assert.assertEquals(2, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));

        Assert.assertTrue(listInstr.get(0) instanceof ASTPush);
        Assert.assertTrue(listInstr.get(1) instanceof ASTNeg);
    }

    @Test
    public void nodeEtTest() throws VisitorException {
        ASTet et = new ASTet(JJTET);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        et.jjtAddChild(vrai, 0);
        et.jjtAddChild(faux, 1);

        var n = (int) compilerVisitor.visit(et, new CompilerData(1, CompilerMode.DEFAULT));
        Assert.assertEquals(5, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }

    @Test
    public void nodeOuTest() throws VisitorException {
        ASTou ou = new ASTou(JJTOU);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        ou.jjtAddChild(vrai, 0);
        ou.jjtAddChild(faux, 1);

        var n = (int) compilerVisitor.visit(ou, new CompilerData(1, CompilerMode.DEFAULT));
        Assert.assertEquals(5, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }


    @Test
    public void nodeEgalBooleenTest() throws VisitorException {
        ASTegal egal = new ASTegal(JJTEGAL);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        egal.jjtAddChild(vrai, 0);
        egal.jjtAddChild(faux, 1);

        var n = (int) compilerVisitor.visit(egal, new CompilerData(1, CompilerMode.DEFAULT));

        //check return
        Assert.assertEquals(3, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }

    @Test
    public void nodeEgalEntierTest() throws VisitorException {
        ASTegal egal = new ASTegal(JJTEGAL);
        ASTnbre nbre1 = new ASTnbre(JJTNBRE);
        nbre1.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(1);
        egal.jjtAddChild(nbre1, 0);
        egal.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(egal, new CompilerData(1, CompilerMode.DEFAULT));


        Assert.assertEquals(3, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }


    @Test
    public void nodeSupTest() throws VisitorException {
        ASTsup sup = new ASTsup(JJTSUP);
        ASTnbre nbre1 = new ASTnbre(JJTNBRE);
        nbre1.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(2);
        sup.jjtAddChild(nbre1, 0);
        sup.jjtAddChild(nbre2, 1);

        var n = (int) compilerVisitor.visit(sup, new CompilerData(1, CompilerMode.DEFAULT));


        Assert.assertEquals(3, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }

    @Test
    public void nodeNbreTest() {
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);

        var n = (int) compilerVisitor.visit(nbre, new CompilerData(1, CompilerMode.DEFAULT));


        Assert.assertEquals(1, n);
        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
    }


    @Test
    public void nodeTabTest() throws VisitorException {
        var ident = makeIdent("t");
        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = new ASTnbre(JJTNBRE);
        index.jjtSetValue(0);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(index, 1);

        ASTnbre value = new ASTnbre(JJTNBRE);
        value.jjtSetValue(3);

        ASTaffectation affectation = new ASTaffectation(JJTAFFECTATION);
        affectation.jjtAddChild(tab, 0);
        affectation.jjtAddChild(value, 1);

        var n = (int) compilerVisitor.visit(affectation, new CompilerData(1, CompilerMode.DEFAULT));


        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(3, n);

    }

    @Test
    public void nodeLongueurTest() {
        ASTlongueur longueur = new ASTlongueur(JJTLONGUEUR);
        longueur.jjtAddChild(makeIdent("t"), 0);

        var n = (int) compilerVisitor.visit(longueur, new CompilerData(1, CompilerMode.DEFAULT));


        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(1, n);
    }

    @Test
    public void nodeMethod2Test() throws VisitorException {
        ASTmethode methode = new ASTmethode(JJTMETHODE);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("f");
        //ent
        ASTentetes entetes = new ASTentetes(JJTENTETES);
        ASTentete entete1 = new ASTentete(JJTENTETE);
        ASTident e1 = new ASTident(JJTIDENT);
        e1.jjtSetValue("e1");
        entete1.jjtAddChild(entier, 0);
        entete1.jjtAddChild(e1, 1);
        ASTentetes entetes_suite = new ASTentetes(JJTENTETES);
        ASTentete entete2 = new ASTentete(JJTENTETE);
        ASTident e2 = new ASTident(JJTIDENT);
        e2.jjtSetValue("e2");
        entete2.jjtAddChild(entier, 0);
        entete2.jjtAddChild(e2, 1);
        ASTenil enil = new ASTenil(JJTEXNIL);
        entetes_suite.jjtAddChild(entete2, 0);
        entetes_suite.jjtAddChild(enil, 1);
        entetes.jjtAddChild(entete1, 0);
        entetes.jjtAddChild(entetes_suite, 1);
        //dvs
        ASTvar var = makeVar("i", (Integer) null);
        ASTvar var2 = makeVar("i2", (Integer) null);
        ASTvnil vnil = new ASTvnil(JJTVNIL);
        ASTvars vars = new ASTvars(JJTVARS);
        ASTvars vars_suite = new ASTvars(JJTVARS);
        vars_suite.jjtAddChild(var2, 0);
        vars_suite.jjtAddChild(vnil, 1);
        vars.jjtAddChild(var, 0);
        vars.jjtAddChild(vars_suite, 1);
        //iss
        ASTinstrs instrs = new ASTinstrs(JJTINSTRS);
        ASTincrement increment = new ASTincrement(JJTINCREMENT);
        ASTident ident2 = new ASTident(JJTIDENT);
        ident2.jjtSetValue("i");
        ASTinil inil = new ASTinil(JJTINIL);
        increment.jjtAddChild(ident2, 0);
        instrs.jjtAddChild(increment, 0);
        instrs.jjtAddChild(inil, 1);

        methode.jjtAddChild(entier, 0);
        methode.jjtAddChild(ident, 1);
        methode.jjtAddChild(entetes, 2);
        methode.jjtAddChild(vars, 3);
        methode.jjtAddChild(instrs, 4);

        var n = (int) compilerVisitor.visit(methode, new CompilerData(1, CompilerMode.DEFAULT));

        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(17, n);
    }


    @Test
    public void nodeAppelETest() throws VisitorException {

        ASTappelE appele = new ASTappelE(JJTAPPELE);
        ASTlistexp listexp = new ASTlistexp(JJTLISTEXP);
        ASTnbre n2 = new ASTnbre(JJTNBRE);
        n2.jjtSetValue(2);
        ASTnbre n3 = new ASTnbre(JJTNBRE);
        n3.jjtSetValue(3);
        ASTexnil exnil = new ASTexnil(JJTEXNIL);
        ASTlistexp listexp_suite = new ASTlistexp(JJTLISTEXP);
        listexp_suite.jjtAddChild(n3, 0);
        listexp_suite.jjtAddChild(exnil, 1);
        listexp.jjtAddChild(n2, 0);
        listexp.jjtAddChild(listexp_suite, 1);
        appele.jjtAddChild(makeIdent("f"), 0);
        appele.jjtAddChild(listexp, 1);

        var n = (int) compilerVisitor.visit(appele, new CompilerData(1, CompilerMode.DEFAULT));

        listInstr = compilerVisitor.getListInstr();
        //System.out.println(CompilerToString.instrToString(compilerVisitor.getListInstr()));
        Assert.assertEquals(7, n);
    }

}
