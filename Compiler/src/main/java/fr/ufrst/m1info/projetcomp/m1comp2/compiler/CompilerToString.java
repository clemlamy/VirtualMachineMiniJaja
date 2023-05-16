package fr.ufrst.m1info.projetcomp.m1comp2.compiler;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.SimpleNode;

import java.util.List;

public class CompilerToString {
    public static String instrToString(List<Node> instrs){
        StringBuilder res = new StringBuilder();
        for(Node instr : instrs){
            res.append(instr.toString().toLowerCase());
            if(instr.jjtGetNumChildren() != 0){
                res.append("(");
                for(int i = 0, l = instr.jjtGetNumChildren() ; i < l ; ++i){
                    SimpleNode child = (SimpleNode) instr.jjtGetChild(i);
                    if(i != l-1){
                        res.append(child.jjtGetValue()).append(",");
                    }else{
                        res.append(child.jjtGetValue());
                    }
                }
                res.append(")");
            }
            res.append("\n");
        }
        return res.toString();
    }
}
