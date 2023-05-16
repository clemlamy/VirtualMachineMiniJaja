package fr.ufrst.m1info.projetcomp.typecheker;

import fr.ufrst.m1info.projetcomp.m1comp2.SymbolTable;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.ASTident;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;


public class TypeChecker {
    private final Node root;
    private final TypeCheckerVisitor visitor;
    private final SymbolTable symbolTable;

    public static final String GLOBAL = "global";
    public static final String MAIN = "main";
    public static final String SEPARATOR = "@";
    public static final String PARAM_SEPARATOR = ",";


    public TypeChecker(Node node,SymbolTable symbolTable){
        root = node;
        this.symbolTable = symbolTable;
        visitor = new TypeCheckerVisitor(symbolTable);
    }

    public void typeCheck() throws VisitorException {
        root.jjtAccept(visitor,null);
    }

    public void astToStringScopedRoot(){
        System.out.println(root.toString());
        astToStringScoped(root.jjtGetChild(0),1);
    }

    public void astToStringScoped(Node node, int i){
        for(int j = 0; j < i ; ++ j){
            System.out.print("\t");
        }
        if(node instanceof ASTident){
            System.out.println("ident " + ((ASTident) node).jjtGetValue());
        }else{
            System.out.println(node.toString());
        }
        for(int j = 0; j < node.jjtGetNumChildren() ; ++j){
            astToStringScoped(node.jjtGetChild(j),i+1);
        }
    }
}
