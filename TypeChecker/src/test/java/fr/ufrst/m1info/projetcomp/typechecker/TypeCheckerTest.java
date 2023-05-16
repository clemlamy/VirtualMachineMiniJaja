package fr.ufrst.m1info.projetcomp.typechecker;

import fr.ufrst.m1info.projetcomp.m1comp2.SymbolTable;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.MiniJaja;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj.ParseException;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.SimpleNode;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;
import fr.ufrst.m1info.projetcomp.typecheker.TypeChecker;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TypeCheckerTest {
    @Test
    public void testOnCompleteFile() throws FileNotFoundException, ParseException, VisitorException {
        File initialFile = new File("src/test/resources/test_typechecker.mjj");
        FileInputStream f = new FileInputStream(initialFile);
        MiniJaja mjj = new MiniJaja(f);
        SimpleNode n = mjj.Start();
        TypeChecker typeChecker = new TypeChecker(n,new SymbolTable());

        typeChecker.typeCheck();
        typeChecker.astToStringScopedRoot();
    }
}
