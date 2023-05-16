package fr.ufrst.m1info.projetcomp.typechecker;

import fr.ufrst.m1info.projetcomp.m1comp2.Quad;
import fr.ufrst.m1info.projetcomp.m1comp2.SymbolException;
import fr.ufrst.m1info.projetcomp.m1comp2.SymbolTable;
import fr.ufrst.m1info.projetcomp.m1comp2.TypeException;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.MiniJaja;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.ParseException;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;
import fr.ufrst.m1info.projetcomp.typecheker.TypeChecker;
import fr.ufrst.m1info.projetcomp.typecheker.TypeCheckerVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Unit test for simple App.
 */
public class TypeCheckerVisitorTest {
    private SymbolTable symbolTable;
    private TypeCheckerVisitor visitor;

    @Before
    public void setup() throws FileNotFoundException {
        symbolTable = new SymbolTable();
        visitor = new TypeCheckerVisitor(symbolTable);
    }

    @Test
    public void identInteger() throws TypeException, SymbolException, VisitorException {
        String id = "var";
        symbolTable.put(new Quad(id+"@global",42, Type.INTEGER, Nature.VAR));
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ident.jjtSetValue(id);
        Assert.assertEquals(Type.INTEGER, visitor.visit(ident, TypeChecker.GLOBAL));
    }

    @Test
    public void identBoolean() throws TypeException, SymbolException, VisitorException {
        String id = "var";
        symbolTable.put(new Quad(id+"@global",true, Type.BOOLEAN, Nature.VAR));
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ident.jjtSetValue(id);
        Assert.assertEquals(Type.BOOLEAN, visitor.visit(ident,TypeChecker.GLOBAL));
    }

    @Test
    public void identMethode() throws TypeException, SymbolException, VisitorException {
        // entetes
        ASTentetes entetes = new ASTentetes(MiniJajaTreeConstants.JJTENTETES);
        ASTentetes entetesChild = new ASTentetes(MiniJajaTreeConstants.JJTENTETES);
        ASTident param1Ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        param1Ident.jjtSetValue("x");
        ASTentier param1Type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);

        ASTident param2Ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        param1Ident.jjtSetValue("y");
        ASTbooleen param2Type = new ASTbooleen(MiniJajaTreeConstants.JJTBOOLEEN);

        ASTentete entete1 = new ASTentete(MiniJajaTreeConstants.JJTENTETE);
        entete1.jjtAddChild(param1Type,0);
        entete1.jjtAddChild(param1Ident,1);
        ASTentete entete2 = new ASTentete(MiniJajaTreeConstants.JJTENTETE);
        entete2.jjtAddChild(param2Type,0);
        entete2.jjtAddChild(param2Ident,1);

        entetesChild.jjtAddChild(entete2,0);
        entetesChild.jjtAddChild(new ASTenil(MiniJajaTreeConstants.JJTENIL),1);

        entetes.jjtAddChild(entete1,0);
        entetes.jjtAddChild(entetesChild,1);

        // componenets
        Object[] methode = new Object[3];
        methode[0] = entetes;
        methode[1] = new ASTvnil(MiniJajaTreeConstants.JJTVNIL);
        methode[2] = new ASTinil(MiniJajaTreeConstants.JJTINIL);


        // ident
        String id = "meth";
        symbolTable.put(new Quad(id+"@global",methode, Type.BOOLEAN, Nature.METH));
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ident.jjtSetValue(id);
        Assert.assertEquals(Type.BOOLEAN, visitor.visit(ident,TypeChecker.GLOBAL));
    }

    @Test
    public void identSymbolDontExists_thenExceptionThrown() {
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ident.jjtSetValue("var");
        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(ident,null)
        );
    }

    @Test
    public void cstNoExp() throws VisitorException {
        String id = "cst";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTcst cst = new ASTcst(MiniJajaTreeConstants.JJTCST);
        cst.jjtAddChild(type,0);
        cst.jjtAddChild(ident,1);
        cst.jjtAddChild(omega,2);

        Assert.assertNull(visitor.visit(cst,null));
    }

    @Test
    public void cstExpValidTypes() throws VisitorException {
        String id = "cst";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTvexp vexp = new ASTvexp(MiniJajaTreeConstants.JJTVEXP);
        ASTnbre nbre = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        ident.jjtSetValue(id);
        nbre.jjtSetValue(42);
        vexp.jjtAddChild(nbre,0);

        ASTcst cst = new ASTcst(MiniJajaTreeConstants.JJTCST);
        cst.jjtAddChild(type,0);
        cst.jjtAddChild(ident,1);
        cst.jjtAddChild(vexp,2);

        Assert.assertNull(visitor.visit(cst,null));
    }

    @Test
    public void cstSymbolAlreadyExists_thenExceptionThrown() throws TypeException, SymbolException {
        String id = "cst";
        symbolTable.put(new Quad(id+TypeChecker.SEPARATOR+TypeChecker.GLOBAL,true, Type.BOOLEAN, Nature.CST));

        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTcst cst = new ASTcst(MiniJajaTreeConstants.JJTCST);
        cst.jjtAddChild(type,0);
        cst.jjtAddChild(ident,1);
        cst.jjtAddChild(omega,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(cst,TypeChecker.GLOBAL)
        );
    }

    @Test
    public void cstInvalidTypes_thenExceptionThrown(){
        String id = "cst";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTvexp vexp = new ASTvexp(MiniJajaTreeConstants.JJTVEXP);
        ASTfaux faux = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ident.jjtSetValue(id);
        faux.jjtSetValue(42);
        vexp.jjtAddChild(faux,0);

        ASTcst cst = new ASTcst(MiniJajaTreeConstants.JJTCST);
        cst.jjtAddChild(type,0);
        cst.jjtAddChild(ident,1);
        cst.jjtAddChild(vexp,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(cst,null)
        );
    }

    @Test
    public void cstVoidType_thenExceptionThrown() {
        String id = "cst";
        ASTrien type = new ASTrien(MiniJajaTreeConstants.JJTRIEN);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTcst cst = new ASTcst(MiniJajaTreeConstants.JJTCST);
        cst.jjtAddChild(type,0);
        cst.jjtAddChild(ident,1);
        cst.jjtAddChild(omega,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(cst,null)
        );
    }


    @Test
    public void varNoExp() throws VisitorException {
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        Assert.assertNull(visitor.visit(var,null));
    }

    @Test
    public void varExpValidTypes() throws VisitorException {
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTvexp vexp = new ASTvexp(MiniJajaTreeConstants.JJTVEXP);
        ASTnbre nbre = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        ident.jjtSetValue(id);
        nbre.jjtSetValue(42);
        vexp.jjtAddChild(nbre,0);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(vexp,2);

        Assert.assertNull(visitor.visit(var,null));
    }

    @Test
    public void varSymbolAlreadyExists_thenExceptionThrown() throws TypeException, SymbolException { // TODO : see if we do that here
        String id = "var";
        symbolTable.put(new Quad(id+"@global",true, Type.BOOLEAN, Nature.VAR));

        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(var,TypeChecker.GLOBAL)
        );
    }

    @Test
    public void varVoidType_thenExceptionThrown() {
        String id = "var";

        ASTrien type = new ASTrien(MiniJajaTreeConstants.JJTRIEN);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(var,null)
        );
    }

    @Test
    public void varInvalidTypes_thenExceptionThrown(){
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTvexp vexp = new ASTvexp(MiniJajaTreeConstants.JJTVEXP);
        ASTfaux faux = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ident.jjtSetValue(id);
        faux.jjtSetValue(42);
        vexp.jjtAddChild(faux,0);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(vexp,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(var,null)
        );
    }

    @Test
    public void omegaIsOMEGA(){
        Assert.assertEquals(Type.OMEGA,visitor.visit(new ASTomega(MiniJajaTreeConstants.JJTOMEGA),null));
    }

    @Test
    public void longueurIsINTEGER(){
        Assert.assertEquals(Type.INTEGER,visitor.visit(new ASTlongueur(MiniJajaTreeConstants.JJTLONGUEUR),null));
    }

    @Test
    public void nbreIsINTEGER(){
        Assert.assertEquals(Type.INTEGER,visitor.visit(new ASTnbre(MiniJajaTreeConstants.JJTNBRE),null));
    }

    @Test
    public void entierIsINTEGER(){
        Assert.assertEquals(Type.INTEGER,visitor.visit(new ASTentier(MiniJajaTreeConstants.JJTENTIER),null));
    }

    @Test
    public void fauxIsBOOLEAN(){
        Assert.assertEquals(Type.BOOLEAN,visitor.visit(new ASTfaux(MiniJajaTreeConstants.JJTFAUX),null));
    }

    @Test
    public void vraiIsBOOLEAN(){
        Assert.assertEquals(Type.BOOLEAN,visitor.visit(new ASTvrai(MiniJajaTreeConstants.JJTVRAI),null));
    }

    @Test
    public void boolIsBOOLEAN(){
        Assert.assertEquals(Type.BOOLEAN,visitor.visit(new ASTbooleen(MiniJajaTreeConstants.JJTBOOLEEN),null));
    }

    @Test
    public void chainIsOMEGA(){
        Assert.assertEquals(Type.OMEGA,visitor.visit(new ASTchaine(MiniJajaTreeConstants.JJTCHAINE),null));
    }

    @Test
    public void rienIsVOID(){
        Assert.assertEquals(Type.VOID,visitor.visit(new ASTrien(MiniJajaTreeConstants.JJTRIEN),null));
    }

    @Test
    public void multValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTmult mult = new ASTmult(MiniJajaTreeConstants.JJTMULT);
        mult.jjtAddChild(op1,0);
        mult.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(mult,null));
    }

    @Test
    public void multInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTmult mult = new ASTmult(MiniJajaTreeConstants.JJTMULT);
        mult.jjtAddChild(op1,0);
        mult.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(mult,null)
        );
    }

    @Test
    public void multInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTmult mult = new ASTmult(MiniJajaTreeConstants.JJTMULT);
        mult.jjtAddChild(op1,0);
        mult.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(mult,null)
        );
    }

    @Test
    public void divValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTdiv div = new ASTdiv(MiniJajaTreeConstants.JJTDIV);
        div.jjtAddChild(op1,0);
        div.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(div,null));
    }

    @Test
    public void divInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTdiv div = new ASTdiv(MiniJajaTreeConstants.JJTDIV);
        div.jjtAddChild(op1,0);
        div.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(div,null)
        );
    }

    @Test
    public void divInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTdiv div = new ASTdiv(MiniJajaTreeConstants.JJTDIV);
        div.jjtAddChild(op1,0);
        div.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(div,null)
        );
    }

    @Test
    public void moinsValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTmoins moins = new ASTmoins(MiniJajaTreeConstants.JJTMOINS);
        moins.jjtAddChild(op1,0);
        moins.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(moins,null));
    }

    @Test
    public void moinsInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTmoins moins = new ASTmoins(MiniJajaTreeConstants.JJTMOINS);
        moins.jjtAddChild(op1,0);
        moins.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(moins,null)
        );
    }

    @Test
    public void moinsInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTmoins moins = new ASTmoins(MiniJajaTreeConstants.JJTMOINS);
        moins.jjtAddChild(op1,0);
        moins.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(moins,null)
        );
    }


    @Test
    public void plusValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTplus plus = new ASTplus(MiniJajaTreeConstants.JJTPLUS);
        plus.jjtAddChild(op1,0);
        plus.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(plus,null));
    }

    @Test
    public void plusInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTplus plus = new ASTplus(MiniJajaTreeConstants.JJTPLUS);
        plus.jjtAddChild(op1,0);
        plus.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(plus,null)
        );
    }

    @Test
    public void plusInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTplus plus = new ASTplus(MiniJajaTreeConstants.JJTPLUS);
        plus.jjtAddChild(op1,0);
        plus.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(plus,null)
        );
    }


    @Test
    public void infValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN,visitor.visit(inf,null));
    }

    @Test
    public void infInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(inf,null)
        );
    }

    @Test
    public void infInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(inf,null)
        );
    }

    @Test
    public void supValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);


        ASTsup sup = new ASTsup(MiniJajaTreeConstants.JJTSUP);
        sup.jjtAddChild(op1,0);
        sup.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN,visitor.visit(sup,null));
    }

    @Test
    public void supInvalidType_thenExceptionThrown() {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTsup sup = new ASTsup(MiniJajaTreeConstants.JJTSUP);
        sup.jjtAddChild(op1,0);
        sup.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(sup,null)
        );
    }

    @Test
    public void supInvalidTypes_thenExceptionThrown() {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);


        ASTsup sup = new ASTsup(MiniJajaTreeConstants.JJTSUP);
        sup.jjtAddChild(op1,0);
        sup.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(sup,null)
        );
    }

    @Test
    public void etValidType() throws VisitorException {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTet et = new ASTet(MiniJajaTreeConstants.JJTET);
        et.jjtAddChild(op1,0);
        et.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN, visitor.visit(et,null));
    }

    @Test
    public void etInvalidType_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTet et = new ASTet(MiniJajaTreeConstants.JJTET);
        et.jjtAddChild(op1,0);
        et.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(et,null)
        );
    }

    @Test
    public void etInvalidTypes_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(24);

        ASTet et = new ASTet(MiniJajaTreeConstants.JJTET);
        et.jjtAddChild(op1,0);
        et.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(et,null)
        );
    }

    @Test
    public void ouValidType() throws VisitorException {
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTou ou = new ASTou(MiniJajaTreeConstants.JJTOU);
        ou.jjtAddChild(op1,0);
        ou.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN, visitor.visit(ou,null));
    }

    @Test
    public void ouInvalidType_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTou ou = new ASTou(MiniJajaTreeConstants.JJTOU);
        ou.jjtAddChild(op1,0);
        ou.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(ou,null)
        );
    }

    @Test
    public void ouInvalidTypes_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(24);

        ASTou ou = new ASTou(MiniJajaTreeConstants.JJTOU);
        ou.jjtAddChild(op1,0);
        ou.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(ou,null)
        );
    }

    @Test
    public void egalValidTypeIntegers() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTegal egal = new ASTegal(MiniJajaTreeConstants.JJTEGAL);
        egal.jjtAddChild(op1,0);
        egal.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN, visitor.visit(egal,null));
    }

    @Test
    public void egalValidTypeBooleans() throws VisitorException {
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);
        ASTfaux op2 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);

        ASTegal egal = new ASTegal(MiniJajaTreeConstants.JJTEGAL);
        egal.jjtAddChild(op1,0);
        egal.jjtAddChild(op2,1);

        Assert.assertEquals(Type.BOOLEAN, visitor.visit(egal,null));
    }

    @Test
    public void egalDifferentType_thenExceptionThrown() {
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTegal egal = new ASTegal(MiniJajaTreeConstants.JJTEGAL);
        egal.jjtAddChild(op1,0);
        egal.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(egal,null)
        );
    }

    @Test
    public void affectationSameTypeIntegers() throws VisitorException {
        // Var decl
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        // vars
        ASTvars vars = new ASTvars(MiniJajaTreeConstants.JJTVARS);
        vars.jjtAddChild(var,0);
        vars.jjtAddChild(new ASTvnil(MiniJajaTreeConstants.JJTVNIL),1);

        Assert.assertNull(vars.jjtAccept(visitor,TypeChecker.GLOBAL));


        // affectation
        ASTident op1 = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        op1.jjtSetValue(id);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTaffectation affectation = new ASTaffectation(MiniJajaTreeConstants.JJTAFFECTATION);
        affectation.jjtAddChild(op1,0);
        affectation.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(affectation,null));
    }

    @Test
    public void affectationSameTypeBooleans() throws VisitorException {
        // Var decl
        String id = "var";
        ASTbooleen type = new ASTbooleen(MiniJajaTreeConstants.JJTBOOLEEN);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        // vars
        ASTvars vars = new ASTvars(MiniJajaTreeConstants.JJTVARS);
        vars.jjtAddChild(var,0);
        vars.jjtAddChild(new ASTvnil(MiniJajaTreeConstants.JJTVNIL),1);

        Assert.assertNull(vars.jjtAccept(visitor,TypeChecker.GLOBAL));


        // affectation
        ASTident op1 = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        op1.jjtSetValue(id);
        ASTfaux op2 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);

        ASTaffectation affectation = new ASTaffectation(MiniJajaTreeConstants.JJTAFFECTATION);
        affectation.jjtAddChild(op1,0);
        affectation.jjtAddChild(op2,1);
        Assert.assertEquals(Type.BOOLEAN,visitor.visit(affectation,null));
    }

    @Test
    public void affectationDifferentTypes_thenExceptionThrown() throws TypeException, SymbolException {
        String id = "var";
        symbolTable.put(new Quad(id+"@global",true, Type.BOOLEAN, Nature.VAR));
        ASTident asTident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        asTident.jjtSetValue("id");
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTaffectation affectation = new ASTaffectation(MiniJajaTreeConstants.JJTAFFECTATION);
        affectation.jjtAddChild(asTident,0);
        affectation.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(affectation,"global")
        );
    }

    @Test
    public void sommeValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTsomme somme = new ASTsomme(MiniJajaTreeConstants.JJTSOMME);
        somme.jjtAddChild(op1,0);
        somme.jjtAddChild(op2,1);

        Assert.assertEquals(Type.INTEGER, visitor.visit(somme,null));
    }

    @Test
    public void sommeBadType_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTsomme somme = new ASTsomme(MiniJajaTreeConstants.JJTSOMME);
        somme.jjtAddChild(op1,0);
        somme.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(somme,null)
        );
    }

    @Test
    public void sommeBadTypes_thenExceptionThrown(){
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTsomme somme = new ASTsomme(MiniJajaTreeConstants.JJTSOMME);
        somme.jjtAddChild(op1,0);
        somme.jjtAddChild(op2,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(somme,null)
        );
    }

    @Test
    public void incrementValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);

        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(op1,0);

        Assert.assertEquals(Type.INTEGER,visitor.visit(increment,null));
    }

    @Test
    public void incrementBadType_thenExceptionThrown(){
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(op1,0);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(increment,null)
        );
    }

    @Test
    public void nonValidType() throws VisitorException {
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTnon non = new ASTnon(MiniJajaTreeConstants.JJTNON);
        non.jjtAddChild(op1,0);

        Assert.assertEquals(Type.BOOLEAN,visitor.visit(non,null));
    }

    @Test
    public void nonBadType_thenExceptionThrown(){
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);

        ASTnon non = new ASTnon(MiniJajaTreeConstants.JJTNON);
        non.jjtAddChild(op1,0);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(non,null)
        );
    }

    @Test
    public void negValidType() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);

        ASTneg neg = new ASTneg(MiniJajaTreeConstants.JJTNEG);
        neg.jjtAddChild(op1,0);

        Assert.assertEquals(Type.INTEGER,visitor.visit(neg,null));
    }

    @Test
    public void negBadType_thenExceptionThrown(){
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTneg neg = new ASTneg(MiniJajaTreeConstants.JJTNEG);
        neg.jjtAddChild(op1,0);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(neg,null)
        );
    }


    @Test
    public void retourInteger() throws VisitorException {
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTretour retour = new ASTretour(MiniJajaTreeConstants.JJTRETOUR);
        retour.jjtAddChild(op1,0);

        Assert.assertEquals(Type.INTEGER, visitor.visit(retour,null));
    }

    @Test
    public void retourBoolean() throws VisitorException {
        ASTvrai op1 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);
        ASTretour retour = new ASTretour(MiniJajaTreeConstants.JJTRETOUR);
        retour.jjtAddChild(op1,0);

        Assert.assertEquals(Type.BOOLEAN, visitor.visit(retour,null));
    }

    @Test
    public void retourVoid_thenExceptionThrown() {
        ASTrien op1 = new ASTrien(MiniJajaTreeConstants.JJTRIEN);
        ASTretour retour = new ASTretour(MiniJajaTreeConstants.JJTRETOUR);
        retour.jjtAddChild(op1,0);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(retour,null)
        );
    }

    @Test
    public void tableauValidSizeType() throws VisitorException {
        String id = "tableau";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTnbre exp = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        exp.jjtSetValue(42);
        ident.jjtSetValue(id);

        ASTtableau tableau = new ASTtableau(MiniJajaTreeConstants.JJTTABLEAU);
        tableau.jjtAddChild(type,0);
        tableau.jjtAddChild(ident,1);
        tableau.jjtAddChild(exp,2);

        Assert.assertNull(visitor.visit(tableau,null));
    }

    @Test
    public void tableauBadValidSizeType_thenExceptionThrown() {
        String id = "tableau";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTvrai exp = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);
        ident.jjtSetValue(id);

        ASTtableau tableau = new ASTtableau(MiniJajaTreeConstants.JJTTABLEAU);
        tableau.jjtAddChild(type,0);
        tableau.jjtAddChild(ident,1);
        tableau.jjtAddChild(exp,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tableau,null)
        );
    }

    @Test
    public void tableauAlreadyExistsSymbol_thenExceptionThrown() throws TypeException, SymbolException {
        String id = "tableau";
        symbolTable.put(new Quad(id+TypeChecker.SEPARATOR+TypeChecker.GLOBAL,42,Type.INTEGER,Nature.TAB));
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTnbre exp = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        exp.jjtSetValue(42);
        ident.jjtSetValue(id);

        ASTtableau tableau = new ASTtableau(MiniJajaTreeConstants.JJTTABLEAU);
        tableau.jjtAddChild(type,0);
        tableau.jjtAddChild(ident,1);
        tableau.jjtAddChild(exp,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tableau,TypeChecker.GLOBAL)
        );
    }

    @Test
    public void tableauVoidType_thenExceptionThrown() {
        String id = "tableau";
        ASTrien type = new ASTrien(MiniJajaTreeConstants.JJTRIEN);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTnbre exp = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        exp.jjtSetValue(42);
        ident.jjtSetValue(id);

        ASTtableau tableau = new ASTtableau(MiniJajaTreeConstants.JJTTABLEAU);
        tableau.jjtAddChild(type,0);
        tableau.jjtAddChild(ident,1);
        tableau.jjtAddChild(exp,2);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tableau,null)
        );
    }

    @Test
    public void tabValidIndexType() throws TypeException, SymbolException, VisitorException {
        String id = "tableau";
        symbolTable.put(new Quad(id+"@global",2,Type.INTEGER,Nature.TAB));
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTnbre exp = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        exp.jjtSetValue(42);
        ident.jjtSetValue(id);

        ASTtab tab = new ASTtab(MiniJajaTreeConstants.JJTTAB);
        tab.jjtAddChild(ident,0);
        tab.jjtAddChild(exp,1);

        Assert.assertEquals(Type.INTEGER,visitor.visit(tab,TypeChecker.GLOBAL));
    }

    @Test
    public void tabBadIndexType_thenExceptionThrown() throws TypeException, SymbolException, VisitorException {
        String id = "tableau";
        symbolTable.put(new Quad(id+TypeChecker.SEPARATOR+TypeChecker.GLOBAL,2,Type.INTEGER,Nature.TAB));
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTfaux exp = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ident.jjtSetValue(id);

        ASTtab tab = new ASTtab(MiniJajaTreeConstants.JJTTAB);
        tab.jjtAddChild(ident,0);
        tab.jjtAddChild(exp,1);
        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tab,TypeChecker.GLOBAL)
        );
    }

    @Test
    public void tabSymbolDontExists_thenExceptionThrown(){
        String id = "tableau";
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTnbre exp = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        exp.jjtSetValue(42);
        ident.jjtSetValue(id);

        ASTtab tab = new ASTtab(MiniJajaTreeConstants.JJTTAB);
        tab.jjtAddChild(ident,0);
        tab.jjtAddChild(exp,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tab,null)
        );
    }

    @Test
    public void siValidConditionType() throws VisitorException {
        // Condition
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        // Instructions if
        ASTinstrs instrs = new ASTinstrs(MiniJajaTreeConstants.JJTINSTRS);
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        ASTnbre i = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        i.jjtSetValue(42);
        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(i,0);

        instrs.jjtAddChild(increment,0);
        instrs.jjtAddChild(inil,1);

        // if
        ASTsi si = new ASTsi(MiniJajaTreeConstants.JJTSI);
        si.jjtAddChild(inf,0);
        si.jjtAddChild(instrs,1);


        Assert.assertNull(visitor.visit(si,null));
    }

    @Test
    public void siBadConditionType_thenExceptionThrown(){
        // Condition
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);

        // Instructions if
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        // if
        ASTsi si = new ASTsi(MiniJajaTreeConstants.JJTSI);
        si.jjtAddChild(op1,0); // ERROR here
        si.jjtAddChild(inil,1);


        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(si,null)
        );
    }

    @Test
    public void siBadErrorInElseInstructions_thenExceptionThrown(){
        // Condition
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        // Instructions else
        ASTinstrs instrs = new ASTinstrs(MiniJajaTreeConstants.JJTINSTRS);
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        ASTvrai bool = new ASTvrai(MiniJajaTreeConstants.JJTVRAI); // ERROR here
        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(bool,0);

        instrs.jjtAddChild(increment,0);
        instrs.jjtAddChild(inil,1);

        // if
        ASTsi si = new ASTsi(MiniJajaTreeConstants.JJTSI);
        si.jjtAddChild(inf,0);
        si.jjtAddChild(inil,1);
        si.jjtAddChild(instrs,2);


        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(si,null)
        );
    }

    @Test
    public void siBadErrorInIfInstructions_thenExceptionThrown(){
        // Condition
        ASTnbre op1 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op1.jjtSetValue(42);
        ASTnbre op2 = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        op2.jjtSetValue(24);

        ASTinf inf = new ASTinf(MiniJajaTreeConstants.JJTINF);
        inf.jjtAddChild(op1,0);
        inf.jjtAddChild(op2,1);

        // Instructions if
        ASTinstrs instrs = new ASTinstrs(MiniJajaTreeConstants.JJTINSTRS);
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        ASTvrai bool = new ASTvrai(MiniJajaTreeConstants.JJTVRAI); // ERROR here
        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(bool,0);

        instrs.jjtAddChild(increment,0);
        instrs.jjtAddChild(inil,1);

        // if
        ASTsi si = new ASTsi(MiniJajaTreeConstants.JJTSI);
        si.jjtAddChild(inf,0);
        si.jjtAddChild(instrs,1);


        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(si,null)
        );
    }

    @Test
    public void tantqueValidConditionType() throws VisitorException {
        // Condition
        ASTvrai cond = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        // Instructions tantque
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        ASTtantque tantque = new ASTtantque(MiniJajaTreeConstants.JJTTANTQUE);
        tantque.jjtAddChild(cond,0);
        tantque.jjtAddChild(inil,1);

        Assert.assertNull(visitor.visit(tantque,null));
    }

    @Test
    public void tantqueBadConditionType_thenExceptionThrown(){
        // Condition
        ASTnbre cond = new ASTnbre(MiniJajaTreeConstants.JJTNBRE);
        cond.jjtSetValue(42);

        // Instruction
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        // Tant que
        ASTtantque tantque = new ASTtantque(MiniJajaTreeConstants.JJTTANTQUE);
        tantque.jjtAddChild(cond,0); // ERROR here
        tantque.jjtAddChild(inil,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tantque,null)
        );
    }

    @Test
    public void tantQueBadErrorInInstructions_thenExceptionThrown(){
        // Condition
        ASTvrai cond = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        // Instructions tantque
        ASTinstrs instrs = new ASTinstrs(MiniJajaTreeConstants.JJTINSTRS);
        ASTinil inil = new ASTinil(MiniJajaTreeConstants.JJTINIL);

        ASTvrai bool = new ASTvrai(MiniJajaTreeConstants.JJTVRAI); // ERROR here
        ASTincrement increment = new ASTincrement(MiniJajaTreeConstants.JJTINCREMENT);
        increment.jjtAddChild(bool,0);

        instrs.jjtAddChild(increment,0);
        instrs.jjtAddChild(inil,1);

        // Tant que
        ASTtantque tantque = new ASTtantque(MiniJajaTreeConstants.JJTTANTQUE);
        tantque.jjtAddChild(cond,0);
        tantque.jjtAddChild(instrs,1);

        Assert.assertThrows(
                VisitorException.class,
                () -> visitor.visit(tantque,null)
        );
    }

    @Test
    public void varsVarVnil() throws VisitorException {
        // var
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        // vars
        ASTvars vars = new ASTvars(MiniJajaTreeConstants.JJTVARS);
        vars.jjtAddChild(var,0);
        vars.jjtAddChild(new ASTvnil(MiniJajaTreeConstants.JJTVNIL),1);

        Assert.assertNull(vars.jjtAccept(visitor,TypeChecker.GLOBAL));
    }

    @Test
    public void declsVarVnil() throws VisitorException {
        // var
        String id = "var";
        ASTentier type = new ASTentier(MiniJajaTreeConstants.JJTENTIER);
        ASTident ident = new ASTident(MiniJajaTreeConstants.JJTIDENT);
        ASTomega omega = new ASTomega(MiniJajaTreeConstants.JJTOMEGA);
        ident.jjtSetValue(id);

        ASTvar var = new ASTvar(MiniJajaTreeConstants.JJTVAR);
        var.jjtAddChild(type,0);
        var.jjtAddChild(ident,1);
        var.jjtAddChild(omega,2);

        // vars
        ASTvars vars = new ASTvars(MiniJajaTreeConstants.JJTVARS);
        vars.jjtAddChild(var,0);
        vars.jjtAddChild(new ASTvnil(MiniJajaTreeConstants.JJTVNIL),1);

        // decls
        ASTdecls decls = new ASTdecls(MiniJajaTreeConstants.JJTDECLS);
        decls.jjtAddChild(vars,0);
        decls.jjtAddChild(new ASTvnil(MiniJajaTreeConstants.JJTVNIL),1);

        Assert.assertNull(decls.jjtAccept(visitor,TypeChecker.GLOBAL));
    }

    @Test
    public void write() throws VisitorException {
        ASTecrire write = new ASTecrire(MiniJajaTreeConstants.JJTECRIRE);
        ASTchaine asTchaine = new ASTchaine(MiniJajaTreeConstants.JJTCHAINE);
        asTchaine.jjtSetValue("Hello world !");

        write.jjtAddChild(asTchaine,0);

        Assert.assertNull(write.jjtAccept(visitor,TypeChecker.GLOBAL));
    }

    @Test
    public void writeLn() throws VisitorException {
        ASTecrireln writeLn = new ASTecrireln(MiniJajaTreeConstants.JJTECRIRE);
        ASTchaine asTchaine = new ASTchaine(MiniJajaTreeConstants.JJTCHAINE);
        asTchaine.jjtSetValue("Hello world !");

        writeLn.jjtAddChild(asTchaine,0);

        Assert.assertNull(writeLn.jjtAccept(visitor,TypeChecker.GLOBAL));
    }

    @Test
    public void methodeValidReturns() throws FileNotFoundException, ParseException, VisitorException {
        File initialFile = new File("src/test/resources/visitMethodeOK.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        typeChecker.typeCheck();
    }

    @Test
    public void methodeBadCall() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeBadCall.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeBadReturnType() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeBadReturnType.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeMissingReturn1() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeMissingReturn1.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeMissingReturn2() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeMissingReturn2.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeMissingReturn3() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeMissingReturn3.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeMissingReturn4() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeMissingReturn4.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }
    @Test
    public void methodeMissingNotExists1() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeNotExists.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void methodeMissingNotExists2() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeNotExists2.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }



    @Test
    public void methodeDuplicated() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/visitMethodeDuplicated.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }

        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void affectation1_exceptionThrown() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/badAffectation1.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void affectation2_exceptionThrown() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/badAffectation2.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void affectation3_exceptionThrown() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/badAffectation3.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void affectation4_exceptionThrown() throws FileNotFoundException, ParseException {
        File initialFile = new File("src/test/resources/badAffectation4.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        try{
            new MiniJaja(f);
        }catch (Error e){
            MiniJaja.ReInit(f);
        }
        SimpleNode n = MiniJaja.Start();
        TypeChecker typeChecker = new TypeChecker(n, new SymbolTable());

        Assert.assertThrows(
                VisitorException.class,
                typeChecker::typeCheck
        );
    }

    @Test
    public void listexp() throws VisitorException {
        // exp
        ASTfaux op1 = new ASTfaux(MiniJajaTreeConstants.JJTFAUX);
        ASTvrai op2 = new ASTvrai(MiniJajaTreeConstants.JJTVRAI);

        ASTet et = new ASTet(MiniJajaTreeConstants.JJTET);
        et.jjtAddChild(op1,0);
        et.jjtAddChild(op2,1);


        ASTlistexp listexp = new ASTlistexp(MiniJajaTreeConstants.JJTLISTEXP);
        listexp.jjtAddChild(et,0);
        listexp.jjtAddChild(new ASTexnil(MiniJajaTreeConstants.JJTEXNIL),1);

        Assert.assertNull(visitor.visit(listexp,null));
    }

    /*
        exnil
        listexp
     */
}
