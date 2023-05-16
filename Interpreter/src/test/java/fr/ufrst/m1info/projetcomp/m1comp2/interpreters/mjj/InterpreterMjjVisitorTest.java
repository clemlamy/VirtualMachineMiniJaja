package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.mjj;

import fr.ufrst.m1info.projetcomp.m1comp2.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;

import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.InterpreterException;
import fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared.InterpreterMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.JajaCodeTreeConstants.JJTWRITE;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.JajaCodeTreeConstants.JJTWRITELN;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.*;

public class InterpreterMjjVisitorTest {

    private InterpreterMjjVisitor interpreterVisitor;
    private Memory memory;
    private SymbolTable symbolTable;

    @Before
    public void init() {
        interpreterVisitor = new InterpreterMjjVisitor();
        symbolTable = new SymbolTable();
        memory = new Memory(symbolTable);

        interpreterVisitor.setMemory(memory);
        interpreterVisitor.setAddress(1);
    }

    private ASTvar createVar(String name) {
        ASTvar var = new ASTvar(JJTVAR);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue(name);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        var.jjtAddChild(ident, 1);
        var.jjtAddChild(nbre, 2);
        return var;
    }

    @Ignore
    @Test
    public void nodeDeclsTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        ASTvnil vnil = new ASTvnil(JJTVNIL);
        ASTdecls decls = new ASTdecls(JJTDECLS);
        decls.jjtAddChild(var, 0);
        decls.jjtAddChild(vnil, 1);

        Object res = decls.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(1, memory.getValue("i"));
    }

    @Ignore
    @Test
    public void nodeVarTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        Object res = var.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(1, memory.getValue("i"));
    }

    @Ignore
    @Test
    public void nodeCstTest() throws InterpreterException, SymbolException, VisitorException {
        ASTcst cst = new ASTcst(JJTCST);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("i");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        cst.jjtAddChild(ident, 1);
        cst.jjtAddChild(nbre, 2);
        Object res = cst.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(1, memory.getValue("i"));
    }

    @Ignore
    @Test
    public void nodeVarsTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        ASTvnil vnil = new ASTvnil(JJTVNIL);
        ASTvars vars = new ASTvars(JJTVARS);
        vars.jjtAddChild(var, 0);
        vars.jjtAddChild(vnil, 1);

        Object res = vars.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(1, memory.getValue("i"));
    }

    @Test
    public void nodePlusTest() throws InterpreterException, SymbolException, StackException, VisitorException {
        ASTplus plus = new ASTplus(JJTPLUS);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(2);
        plus.jjtAddChild(nbre, 0);
        plus.jjtAddChild(nbre2, 1);

        Object res = plus.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(3, res);
    }

    @Test
    public void nodeMoinsTest() throws InterpreterException, SymbolException, StackException, VisitorException {
        ASTmoins moins = new ASTmoins(JJTMOINS);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(3);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(1);
        moins.jjtAddChild(nbre, 0);
        moins.jjtAddChild(nbre2, 1);

        Object res = moins.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(2, res);
    }

    @Test
    public void nodeMultTest() throws InterpreterException, SymbolException, StackException, VisitorException {
        ASTmult mult = new ASTmult(JJTMULT);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(2);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(3);
        mult.jjtAddChild(nbre, 0);
        mult.jjtAddChild(nbre2, 1);

        Object res = mult.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(6, res);
    }

    @Test
    public void nodeDivTest() throws InterpreterException, SymbolException, StackException, VisitorException {
        ASTdiv div = new ASTdiv(MiniJajaTreeConstants.JJTDIV);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(6);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(3);
        div.jjtAddChild(nbre, 0);
        div.jjtAddChild(nbre2, 1);

        Object res = div.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(2, res);
    }

    @Test
    public void nodeNotTest() throws InterpreterException, VisitorException {
        ASTnon not = new ASTnon(JJTNON);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        not.jjtAddChild(vrai, 0);

        Object res = not.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(false, res);
    }

    @Ignore
    @Test
    public void nodeAffectationTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        Object res = var.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTaffectation affect = new ASTaffectation(JJTAFFECTATION);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("i");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(3);
        affect.jjtAddChild(ident, 0);
        affect.jjtAddChild(nbre, 1);

        res = affect.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(3, memory.getValue("i"));
    }

    @Ignore
    @Test
    public void nodeSommeTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        Object res = var.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTsomme somme = new ASTsomme(JJTSOMME);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("i");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(3);
        somme.jjtAddChild(ident, 0);
        somme.jjtAddChild(nbre, 1);

        res = somme.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(4, memory.getValue("i"));
    }
    @Test
    public void nodeSommeTTest() throws InterpreterException, SymbolException, VisitorException, ArrayException, HeapException, StackException {
        //decl tableau1
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        Object res = tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = new ASTnbre(JJTNBRE);
        index.jjtSetValue(0);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(index, 1);

        ASTnbre value = new ASTnbre(JJTNBRE);
        value.jjtSetValue(3);
        ASTnbre value2 = new ASTnbre(JJTNBRE);
        value2.jjtSetValue(5);

        ASTaffectation affectation = new ASTaffectation(JJTAFFECTATION);
        affectation.jjtAddChild(tab, 0);
        affectation.jjtAddChild(value, 1);
        res = affectation.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTsomme sommet = new ASTsomme(JJTSOMME);
        sommet.jjtAddChild(tab,0);
        sommet.jjtAddChild(value2,1);

        res = sommet.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(8, memory.getValueT("t", 0));
    }

    @Ignore
    @Test
    public void nodeIncrementTest() throws InterpreterException, SymbolException, VisitorException {
        ASTvar var = createVar("i");
        Object res = var.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTincrement increment = new ASTincrement(JJTINCREMENT);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("i");
        increment.jjtAddChild(ident, 0);

        res = increment.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(2, memory.getValue("i"));
    }

    @Test
    public void nodeIncrementTTest() throws InterpreterException, SymbolException, VisitorException, ArrayException, HeapException, StackException {
        //decl tableau1
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        Object res = tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

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
        res = affectation.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTincrement increment = new ASTincrement(JJTINCREMENT);
        increment.jjtAddChild(tab,0);

        res = increment.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);

        //check is present in memory
        Assert.assertEquals(4, memory.getValueT("t", 0));
    }

    @Test
    public void nodeExnilTest() throws InterpreterException, SymbolException, VisitorException {
        ASTexnil exnil = new ASTexnil(JJTEXNIL);

        Object res = exnil.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);
    }

    @Test
    public void nodeNegTest() throws InterpreterException, SymbolException, VisitorException {
        ASTneg neg = new ASTneg(MiniJajaTreeConstants.JJTNEG);
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        neg.jjtAddChild(nbre, 0);

        Object res = neg.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(-1, res);
    }

    @Test
    public void nodeEtTest() throws InterpreterException, VisitorException {
        ASTet et = new ASTet(JJTET);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        et.jjtAddChild(vrai, 0);
        et.jjtAddChild(faux, 1);

        Object res = et.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(false, res);
    }

    @Test
    public void nodeOuTest() throws InterpreterException, VisitorException {
        ASTou ou = new ASTou(JJTOU);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        ou.jjtAddChild(vrai, 0);
        ou.jjtAddChild(faux, 1);

        Object res = ou.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(true, res);
    }

    @Test
    public void nodeEgalBooleenTest() throws InterpreterException, VisitorException {
        ASTegal egal = new ASTegal(JJTEGAL);
        ASTvrai vrai = new ASTvrai(JJTVRAI);
        ASTfaux faux = new ASTfaux(JJTFAUX);
        egal.jjtAddChild(vrai, 0);
        egal.jjtAddChild(faux, 1);

        Object res = egal.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(false, res);
    }

    @Test
    public void nodeEgalEntierTest() throws InterpreterException, VisitorException {
        ASTegal egal = new ASTegal(JJTEGAL);
        ASTnbre nbre1 = new ASTnbre(JJTNBRE);
        nbre1.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(1);
        egal.jjtAddChild(nbre1, 0);
        egal.jjtAddChild(nbre2, 1);

        Object res = egal.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(true, res);
    }

    @Test
    public void nodeInfTest() throws InterpreterException, VisitorException {
        ASTinf inf = new ASTinf(JJTINF);
        ASTnbre nbre1 = new ASTnbre(JJTNBRE);
        nbre1.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(2);
        inf.jjtAddChild(nbre1, 0);
        inf.jjtAddChild(nbre2, 1);

        Object res = inf.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(true, res);
    }

    @Test
    public void nodeSupTest() throws InterpreterException, VisitorException {
        ASTsup sup = new ASTsup(JJTSUP);
        ASTnbre nbre1 = new ASTnbre(JJTNBRE);
        nbre1.jjtSetValue(1);
        ASTnbre nbre2 = new ASTnbre(JJTNBRE);
        nbre2.jjtSetValue(2);
        sup.jjtAddChild(nbre1, 0);
        sup.jjtAddChild(nbre2, 1);

        Object res = sup.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(false, res);
    }

    @Test
    public void nodeVraiTest() throws InterpreterException, VisitorException {
        ASTvrai vrai = new ASTvrai(JJTVRAI);

        Object res = vrai.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(true, res);
    }

    @Test
    public void nodeFalseTest() throws InterpreterException, VisitorException {
        ASTfaux faux = new ASTfaux(JJTFAUX);

        Object res = faux.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(false, res);
    }

    @Test
    public void nodeNbreTest() throws InterpreterException, VisitorException {
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);

        Object res = nbre.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(1, res);
    }

    @Test
    public void nodeTableauTest() throws InterpreterException, SymbolException, VisitorException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(2);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(1, memory.getValue("t"));
    }


    @Test
    public void nodeTableauNegativeSizeTest() throws InterpreterException, SymbolException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(2);
        ASTneg neg = new ASTneg(JJTNEG);
        neg.jjtAddChild(nbre,0);
        tableau.jjtAddChild(entier,0);
        tableau.jjtAddChild(ident,1);
        tableau.jjtAddChild(neg,2);

        Assert.assertThrows(
                InterpreterException.class,
                () -> tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT)
        );
    }

    @Test
    public void nodeTabTest() throws InterpreterException, ArrayException, HeapException, StackException, VisitorException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        Object res = tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

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

        res = affectation.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        res = tab.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(3, memory.getValueT("t", 0));
        Assert.assertEquals(3, res);
    }

    @Test
    public void nodeTabNegativeIndexTest() throws InterpreterException, ArrayException, HeapException, StackException, VisitorException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = new ASTnbre(JJTNBRE);
        index.jjtSetValue(1);
        ASTneg neg = new ASTneg(JJTNEG);
        neg.jjtAddChild(index,0);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(neg, 1);

        //check return
        Assert.assertThrows(
                InterpreterException.class,
                () -> tab.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT)
        );
    }

    @Test
    public void nodeTabIndexOutOfBoundTest() throws InterpreterException, ArrayException, HeapException, StackException, VisitorException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(1);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTtab tab = new ASTtab(JJTTAB);
        ASTnbre index = new ASTnbre(JJTNBRE);
        index.jjtSetValue(1);
        tab.jjtAddChild(ident, 0);
        tab.jjtAddChild(index, 1);

        //check return
        Assert.assertThrows(
                InterpreterException.class,
                () -> tab.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT)
        );
    }

    @Test
    public void nodeEntierTest() throws InterpreterException, VisitorException {
        ASTentier entier = new ASTentier(JJTENTIER);

        Object res = entier.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(Type.INTEGER, res);
    }

    @Test
    public void nodeBooleenTest() throws InterpreterException, VisitorException {
        ASTbooleen booleen = new ASTbooleen(JJTBOOLEEN);

        Object res = booleen.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(Type.BOOLEAN, res);
    }

    @Test
    public void nodeChaineTest() throws InterpreterException, VisitorException {
        ASTchaine chaine = new ASTchaine(JJTCHAINE);
        chaine.jjtSetValue("hello world");

        Object res = chaine.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals("hello world", res);
    }

    @Test
    public void nodeLongueurTest() throws InterpreterException, ArrayException, HeapException, StackException, VisitorException {
        ASTtableau tableau = new ASTtableau(JJTTABLEAU);
        ASTentier entier = new ASTentier(JJTENTIER);
        ASTident ident = new ASTident(JJTIDENT);
        ident.jjtSetValue("t");
        ASTnbre nbre = new ASTnbre(JJTNBRE);
        nbre.jjtSetValue(3);
        tableau.jjtAddChild(entier, 0);
        tableau.jjtAddChild(ident, 1);
        tableau.jjtAddChild(nbre, 2);

        Object res = tableau.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTlongueur longueur = new ASTlongueur(JJTLONGUEUR);
        longueur.jjtAddChild(ident, 0);

        res = longueur.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertEquals(3, res);
    }

    @Test
    public void nodeMethodTest() throws InterpreterException, VisitorException, SymbolException {
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
        ASTvar var = createVar("i");
        ASTvar var2 = createVar("i2");
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

        Object res = methode.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        //check return
        Assert.assertNull(res);
        Assert.assertNotNull(memory.getSymbolTable().get("f"));
    }

    @Ignore
    @Test
    public void nodeAppelITest() throws InterpreterException, VisitorException {
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
        ASTvar var = createVar("i");
        ASTvar var2 = createVar("i2");
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

        Object res = methode.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        ASTappelI appeli = new ASTappelI(JJTAPPELI);
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
        appeli.jjtAddChild(ident, 0);
        appeli.jjtAddChild(listexp, 1);

        res = appeli.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        //check return
        Assert.assertEquals(null, res);
    }


    @Ignore
    @Test
    public void nodeAppelETest() throws InterpreterException, TypeException, SymbolException, VisitorException {
        memory.declVar("c", Type.toString(Type.OMEGA), Type.OMEGA);

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
        ASTvar var = createVar("i");
        ASTvar var2 = createVar("i2");
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
        ASTretour retour = new ASTretour(JJTRETOUR);
        retour.jjtAddChild(ident2, 0);
        ASTinil inil = new ASTinil(JJTINIL);
        increment.jjtAddChild(ident2, 0);
        instrs.jjtAddChild(increment, 0);
        ASTinstrs instrs_suite = new ASTinstrs(JJTINSTRS);
        instrs_suite.jjtAddChild(retour, 0);
        instrs_suite.jjtAddChild(inil, 1);
        instrs.jjtAddChild(instrs_suite, 1);

        methode.jjtAddChild(entier, 0);
        methode.jjtAddChild(ident, 1);
        methode.jjtAddChild(entetes, 2);
        methode.jjtAddChild(vars, 3);
        methode.jjtAddChild(instrs, 4);

        Object res = methode.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

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
        appele.jjtAddChild(ident, 0);
        appele.jjtAddChild(listexp, 1);

        res = appele.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        //check return
        Assert.assertEquals(2, res);
    }


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

        // Interpretation
        Object res = nodeClass.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        Assert.assertNull(res);

    }

    @Test
    public void nodeClasseTest() throws VisitorException {
        var nodeDecls1 = new ASTdecls(JJTDECLS);
        var nodeDecls2 = new ASTdecls(JJTDECLS);

        nodeDecls1.jjtAddChild(makeVar("a", false), 0);
        nodeDecls1.jjtAddChild(nodeDecls2, 1);

        nodeDecls2.jjtAddChild(makeVar("b", 4), 0);
        nodeDecls2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var nodeVars1 = new ASTvars(JJTVARS);
        var nodeVars2 = new ASTvars(JJTVARS);

        nodeVars1.jjtAddChild(makeVar("y", true), 0);
        nodeVars1.jjtAddChild(nodeVars2, 1);

        nodeVars2.jjtAddChild(makeVar("x", false), 0);
        nodeVars2.jjtAddChild(new ASTvnil(JJTVNIL), 1);

        var nodeMain = new ASTmain(JJTMAIN);

        nodeMain.jjtAddChild(nodeVars1, 0);
        nodeMain.jjtAddChild(new ASTinil(JJTINIL), 1);

        var nodeClass = new ASTclasse(JJTCLASSE);

        nodeClass.jjtAddChild(makeIdent("C"), 0);
        nodeClass.jjtAddChild(nodeDecls1, 1);
        nodeClass.jjtAddChild(nodeMain, 2);

        // Interpretation
        Object res = nodeClass.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        Assert.assertNull(res);
    }

    @Test
    public void nodeIdentTest() throws VisitorException {
        var nodeIdent = makeIdent("ident");
        var nodeVar1 = makeVar("ident", true);

        // Interpretation
        Object res = nodeVar1.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        res = nodeIdent.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        Assert.assertTrue((Boolean) res);
    }

    @Test
    public void nodeVnilTest() throws VisitorException {
        var nodeVnil = new ASTvnil(JJTVNIL);

        Object res = nodeVnil.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        Assert.assertNull(res);
    }


    @Test
    public void nodeEnilTest() throws VisitorException {
        var nodeEnil = new ASTenil(JJTENIL);

        Object res = nodeEnil.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);

        Assert.assertNull(res);
    }

    @Test
    public void nodeWriteTest() throws VisitorException {
        var nodeWrite = new ASTecrire(JJTWRITE);
        var nodeString = new ASTchaine(JJTCHAINE);
        nodeString.jjtSetValue("Hello World!");

        nodeWrite.jjtAddChild(nodeString, 0);

        nodeWrite.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        Assert.assertEquals("Hello World!",interpreterVisitor.toString());
    }

    @Test
    public void nodeWritelnTest() throws VisitorException {
        var nodeWriteln = new ASTecrireln(JJTWRITELN);
        var nodeString = new ASTchaine(JJTCHAINE);
        nodeString.jjtSetValue("Hello World!");

        nodeWriteln.jjtAddChild(nodeString, 0);

        nodeWriteln.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        Assert.assertEquals("Hello World!\n",interpreterVisitor.toString());
    }

    @Test
    public void nodeIfTest() throws VisitorException, SymbolException, TypeException {
        var nodeIf = new ASTsi(JJTSI);
        var nodeInstr1 = new ASTinstrs(JJTINSTRS);
        var nodeInstr2 = new ASTinstrs(JJTINSTRS);
        var nodeAssignment1 = new ASTaffectation(JJTAFFECTATION);
        var nodeAssignment2 = new ASTaffectation(JJTAFFECTATION);

        memory.declVar("x", 0, Type.INTEGER);

        nodeIf.jjtAddChild(new ASTvrai(JJTVRAI), 0);
        nodeIf.jjtAddChild(nodeInstr1, 1);
        nodeIf.jjtAddChild(nodeInstr2, 2);

        nodeInstr1.jjtAddChild(nodeAssignment1, 0);
        nodeInstr1.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeInstr2.jjtAddChild(nodeAssignment2, 0);
        nodeInstr2.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeAssignment1.jjtAddChild(makeIdent("x"), 0);
        nodeAssignment1.jjtAddChild(makeNumber(42), 1);

        nodeAssignment2.jjtAddChild(makeIdent("x"), 0);
        nodeAssignment2.jjtAddChild(makeNumber(24), 1);

        nodeIf.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        Assert.assertEquals(42,memory.getValue("x"));
    }

    @Test
    public void nodeWhileTest() throws VisitorException, SymbolException, TypeException {
        var nodeWhile = new ASTtantque(JJTTANTQUE);
        var nodeInstrs = new ASTinstrs(JJTINSTRS);
        var nodeAssignment = new ASTaffectation(JJTAFFECTATION);

        memory.declVar("x", 0, Type.INTEGER);

        ASTnon non = new ASTnon(JJTNON);
        ASTegal egal = new ASTegal(JJTEGAL);
        egal.jjtAddChild(makeNumber(1),0);
        egal.jjtAddChild(makeIdent("x"),1);
        non.jjtAddChild(egal,0);

        nodeWhile.jjtAddChild(non, 0);
        nodeWhile.jjtAddChild(nodeInstrs, 1);

        nodeInstrs.jjtAddChild(nodeAssignment, 0);
        nodeInstrs.jjtAddChild(new ASTinil(JJTINIL), 1);

        nodeAssignment.jjtAddChild(makeIdent("x"), 0);
        nodeAssignment.jjtAddChild(makeNumber(1), 1);

        nodeWhile.jjtAccept(interpreterVisitor,  InterpreterMode.DEFAULT);
        Assert.assertEquals(1,memory.getValue("x"));
    }
}