package fr.ufrst.m1info.projetcomp.m1comp2.analyser.mjj;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;

public class MiniJajaDumpVisitor implements MiniJajaVisitor {

    private int indent = 0;

    private String indentString() {
        return " ".repeat(Math.max(0, indent));
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws VisitorException {
        System.out.println(indentString() + node +
                ": acceptor not implemented in subclass?");

        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    @Override
    public Object visit(ASTStart node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTclasse node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTident node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTdecls node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTvnil node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTcst node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTtableau node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTmethode node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTvar node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTvars node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTvexp node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTomega node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTmain node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTentetes node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTenil node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTentete node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTinstrs node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTinil node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTinstr node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTretour node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTecrire node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTecrireln node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTsi node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTtantque node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTappelI node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTtab node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTaffectation node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTsomme node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTincrement node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTlistexp node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTexnil node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTnon node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTneg node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTet node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTou node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTegal node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTinf node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTsup node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTplus node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTmoins node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTmult node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTdiv node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTlongueur node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTvrai node, Object data) {
        try {
            return displayNode(node, data);
        } catch (VisitorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object visit(ASTfaux node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTappelE node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTnbre node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTrien node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTentier node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTbooleen node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    @Override
    public Object visit(ASTchaine node, Object data) throws VisitorException {
        return displayNode(node, data);
    }

    private Object displayNode(SimpleNode node, Object data) throws VisitorException {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

}
