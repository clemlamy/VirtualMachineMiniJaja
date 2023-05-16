package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Test;

public class TypeTest {
    @Test
    public void test_booleanToString(){
        Assert.assertEquals("BOOLEAN", Type.toString(Type.BOOLEAN));
    }

    @Test
    public void test_integerToString(){
        Assert.assertEquals("INTEGER",Type.toString(Type.INTEGER));
    }

    @Test
    public void test_voidToString(){
        Assert.assertEquals("VOID",Type.toString(Type.VOID));
    }

    @Test
    public void test_omegaToString(){
        Assert.assertEquals("?",Type.toString(Type.OMEGA));
    }
}
