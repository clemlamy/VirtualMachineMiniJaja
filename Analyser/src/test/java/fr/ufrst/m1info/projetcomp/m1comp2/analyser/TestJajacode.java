package fr.ufrst.m1info.projetcomp.m1comp2.analyser;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.jjc.JajaCode;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.jjc.*;
import fr.ufrst.m1info.projetcomp.m1comp2.analyser.jjc.ParseException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

public class TestJajacode {
    @Ignore
    public void test_classe()throws IOException, ParseException {
        File initialFile = new File("src/test/java/fr/ufrst/m1info/projetcomp/m1comp2/analyser/tree1.jjc");
        FileInputStream f = new FileInputStream(initialFile);
        JajaCode t = new JajaCode(f);

        Token s1 = t.getNextToken();
        Assert.assertEquals("Init",s1.toString());

        Token s2 = t.getNextToken();
        Assert.assertEquals("Push",s2.toString());

        t.getNextToken();
        t.getNextToken();
        t.getNextToken();
        Token s3 = t.getNextToken();
        Assert.assertEquals("New",s3.toString());
    }
}
