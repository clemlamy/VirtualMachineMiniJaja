package fr.ufrst.m1info.projetcomp.m1comp2.analyser;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.*;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.MiniJajaDumpVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

import java.io.*;

public class TestAnalyser {
    @Test
    public void test_helloworld() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln(\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);

        Assert.assertEquals("Start",n.toString());
        Assert.assertTrue(n instanceof ASTStart);
        Assert.assertEquals(1,n.jjtGetNumChildren());

        Node _class = n.jjtGetChild(0);
        Assert.assertEquals("classe",_class.toString());
        Assert.assertTrue(_class instanceof ASTclasse);

        Node _ident = _class.jjtGetChild(0);
        Assert.assertEquals("ident",_ident.toString());
        Assert.assertTrue(_ident instanceof ASTident);

        Node _vnil1 = _class.jjtGetChild(1);
        Assert.assertEquals("vnil",_vnil1.toString());
        Assert.assertTrue(_vnil1 instanceof ASTvnil);

        Node _main = _class.jjtGetChild(2);
        Assert.assertEquals("main",_main.toString());
        Assert.assertTrue(_main instanceof ASTmain);

        Node _vnil = _main.jjtGetChild(0);
        Assert.assertEquals("vnil",_vnil.toString());
        Assert.assertTrue(_vnil instanceof ASTvnil);

        Node _instrs = _main.jjtGetChild(1);
        Assert.assertEquals("instrs",_instrs.toString());
        Assert.assertTrue(_instrs instanceof ASTinstrs);

        Node _ecrireln = _instrs.jjtGetChild(0);
        Assert.assertEquals("ecrireln",_ecrireln.toString());
        Assert.assertTrue(_ecrireln instanceof ASTecrireln);

        Node _inil = _instrs.jjtGetChild(1);
        Assert.assertEquals("inil",_inil.toString());
        Assert.assertTrue(_inil instanceof ASTinil);

    }

    @Test
    public void test_affectation() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class a { int x=3; main { x=2;}}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);

        Assert.assertEquals("Start",n.toString());
        Assert.assertTrue(n instanceof ASTStart);
        Assert.assertEquals(1,n.jjtGetNumChildren());

        Node _class = n.jjtGetChild(0);
        Assert.assertEquals("classe",_class.toString());
        Assert.assertTrue(_class instanceof ASTclasse);

        Node _ident = _class.jjtGetChild(0);
        Assert.assertEquals("ident",_ident.toString());
        Assert.assertTrue(_ident instanceof ASTident);

        Node _main = _class.jjtGetChild(2);
        Assert.assertEquals("main",_main.toString());
        Assert.assertTrue(_main instanceof ASTmain);

        Node _vnil = _main.jjtGetChild(0);
        Assert.assertEquals("vnil",_vnil.toString());
        Assert.assertTrue(_vnil instanceof ASTvnil);

        Node _instrs = _main.jjtGetChild(1);
        Assert.assertEquals("instrs",_instrs.toString());
        Assert.assertTrue(_instrs instanceof ASTinstrs);

        Node _affectation = _instrs.jjtGetChild(0);
        Assert.assertEquals("affectation",_affectation.toString());
        Assert.assertTrue(_affectation instanceof ASTaffectation);

        Node _inil = _instrs.jjtGetChild(1);
        Assert.assertEquals("inil",_inil.toString());
        Assert.assertTrue(_inil instanceof ASTinil);

    }

    @Test
    public void test_main() {
        SimpleNode n = new SimpleNode(JJTMAIN);
        Assert.assertEquals("main",n.toString());
    }

    @Test
    public void test_complet() {
        ASTStart n1 = new ASTStart(JJTSTART);
        ASTclasse n2 = new ASTclasse(JJTCLASSE);
        Assert.assertEquals("Start",n1.toString());
        Assert.assertEquals("classe",n2.toString());
    }

    //Test erreur

    @Test(expected = ParseException.class)
    public void test_no_class() throws IOException, ParseException, VisitorException {
        String string = "c {\n" +
                "    main {\n" +
                "        writeln(\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_classname() throws IOException, ParseException, VisitorException {
        String string = "class {\n" +
                "    main {\n" +
                "        writeln(\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_main() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    {\n" +
                "        writeln(\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_wrong_instr() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writ(\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_opening_quote() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln(hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_closing_quote() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln(\"hello);\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_opening_parenthesis() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln\"hello\");\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_closing_parenthesis() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln(\"hello\";\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja t = new MiniJaja(f);
        SimpleNode n = t.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_dot() throws IOException, ParseException, VisitorException {
        String string = "class c {\n" +
                "    main {\n" +
                "        writeln(\"hello\")\n" +
                "    }\n" +
                "}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_equal() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class a { int x 3; main { x=2;}}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_int() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class a { x = 3; main { x=2;}}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = NullPointerException.class)
    public void test_no_val_name() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class a { int = 3; main { x=2;}}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }

    @Test(expected = ParseException.class)
    public void test_no_dot2() throws IOException, ParseException, VisitorException {
        /*File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.mjj");
        FileInputStream f = new FileInputStream(initialFile);*/
        String string = "class a { int x = 3 main { x=2;}}";
        InputStream f = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")));
        MiniJaja.ReInit(f);
        SimpleNode n = MiniJaja.Start();
        MiniJajaVisitor dumpVisitor = new MiniJajaDumpVisitor();
        n.jjtAccept(dumpVisitor, null);
    }
}
