package fr.ufrst.m1info.projetcomp.typecheker;

import fr.ufrst.m1info.projetcomp.m1comp2.*;
import fr.ufrst.m1info.projetcomp.m1comp2.SymbolTable;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Nature;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;

import java.util.ArrayList;
import java.util.List;


public class TypeCheckerVisitor implements MiniJajaVisitor {
    private final SymbolTable symbolTable;

    public TypeCheckerVisitor(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws VisitorException {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTStart node, Object data) throws VisitorException {
        data = node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTclasse node, Object data) throws VisitorException {
        Node decls = node.jjtGetChild(1);
        Node main = node.jjtGetChild(2);

        decls.jjtAccept(this,TypeChecker.GLOBAL);
        main.jjtAccept(this,TypeChecker.GLOBAL);
        return null;
    }

    @Override
    public Object visit(ASTident node, Object data) throws VisitorException{
        String id = (String) node.jjtGetValue();
        Quad quad;
        if(id.contains("@")){
            try {
                quad = symbolTable.get(id);
                //node.jjtSetValue(id);
            } catch (SymbolException e) {
                throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",id),node.getLine(),node.getColumn());
            }
        }else{

            try {
                quad = symbolTable.get(id + TypeChecker.SEPARATOR + data);
                node.jjtSetValue(id + TypeChecker.SEPARATOR + data);
            } catch (SymbolException e) {
                try{
                    quad = symbolTable.get(id + TypeChecker.SEPARATOR + TypeChecker.GLOBAL);
                    node.jjtSetValue(id + TypeChecker.SEPARATOR + TypeChecker.GLOBAL);
                }catch (SymbolException ebis){
                    throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",id),node.getLine(),node.getColumn());
                }
            }
        }

        if(quad.getNature().equals(Nature.METH)){
            Object[] methComponents = (Object[])quad.getValue();
            ASTentetes entetes = (ASTentetes) methComponents[0];
            SimpleNode vars = (SimpleNode) methComponents[1];
            SimpleNode instrs = (SimpleNode) methComponents[2];

            entetes.jjtAccept(this,TypeChecker.GLOBAL);
            vars.jjtAccept(this,TypeChecker.GLOBAL);
            instrs.jjtAccept(this,TypeChecker.GLOBAL);
        }
        return quad.getType();
    }

    @Override
    public Object visit(ASTdecls node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTvnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTcst node, Object data) throws VisitorException {
        final String SCOPE = (String) data;
        Node type = node.jjtGetChild(0);
        SimpleNode ident = (SimpleNode) node.jjtGetChild(1);
        Node exp = node.jjtGetChild(2);


        if(exp.jjtGetNumChildren()>0 && exp.jjtGetChild(0) instanceof ASTident){
            String idExp = (String) ((SimpleNode) exp.jjtGetChild(0)).jjtGetValue();

            Quad quadExp;
            try {
                quadExp = symbolTable.get(idExp + TypeChecker.SEPARATOR + data);
            } catch (SymbolException e) {
                try{
                    quadExp = symbolTable.get(idExp + TypeChecker.SEPARATOR + TypeChecker.GLOBAL);
                }catch (SymbolException ebis){
                    throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",idExp),node.getLine(),node.getColumn());
                }
            }

            if(quadExp.getNature().equals(Nature.TAB)){
                throw new TypeCheckerException("An array can't be affected to a constant",node.getLine(),node.getColumn());
            }
        }

        String id = (String) ident.jjtGetValue();
        Type exprType = (Type) exp.jjtAccept(this,data);
        Type expectedType = (Type) type.jjtAccept(this,data);

        if(expectedType.equals(Type.VOID)){
            throw new TypeCheckerException(String.format("Constant cannot be of type %s",type),node.getLine(),node.getColumn());
        }

        if(!exprType.equals(Type.OMEGA) && !exprType.equals(expectedType)){
            throw new TypeCheckerException(String.format("Value of type %s is not compatible with the constant %s of type %s",exprType,id,expectedType),node.getLine(),node.getColumn());
        }
        Quad quad;
        id += TypeChecker.SEPARATOR + SCOPE;
        try {
            quad = new Quad(id,null,expectedType, Nature.CST);
        } catch (TypeException e) {
            throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",id,null,expectedType,Nature.CST),node.getLine(),node.getColumn());
        }
        try {
            symbolTable.put(quad);
            ident.jjtSetValue(id);
        } catch (SymbolException e) {
            throw new TypeCheckerException("The symbol \"" + id + "\" already exists.",node.getLine(),node.getColumn());
        }
        return null;
    }

    @Override
    public Object visit(ASTtableau node, Object data) throws VisitorException {
        final String SCOPE = (String) data;
        Node type = node.jjtGetChild(0);
        SimpleNode ident = (SimpleNode) node.jjtGetChild(1);
        Node exp = node.jjtGetChild(2);

        String id = (String) ident.jjtGetValue();
        Type exprType = (Type) exp.jjtAccept(this,data);
        Type expectedType = (Type) type.jjtAccept(this,data);

        if(!exprType.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Array size must be an Integer (given : %s)",exprType),node.getLine(),node.getColumn());
        }

        if(expectedType.equals(Type.VOID)){
            throw new TypeCheckerException("Array size cannot VOID type ",node.getLine(),node.getColumn());
        }

        Quad quad;
        id += TypeChecker.SEPARATOR + SCOPE;
        try {
            quad = new Quad(id,null,expectedType, Nature.TAB);
        } catch (TypeException e) {
            throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",id,null,expectedType,Nature.TAB),node.getLine(),node.getColumn());
        }
        try {
            symbolTable.put(quad);
            ident.jjtSetValue(id);
        } catch (SymbolException e) {
            throw new TypeCheckerException("The symbol \"" + id + "\" already exists.",node.getLine(),node.getColumn());
        }
        return null;
    }

    @Override
    public Object visit(ASTmethode node, Object data) throws VisitorException {
        SimpleNode ident = (SimpleNode) node.jjtGetChild(1);
        String id = (String) ident.jjtGetValue();

        final String SCOPE;
        if(node.jjtGetChild(2) instanceof ASTenil){
            SCOPE = id+":";
        }else{
            SCOPE = headerToString((ASTentetes) node.jjtGetChild(2),id,null);
        }
        System.out.println(SCOPE);
        id += TypeChecker.SEPARATOR + SCOPE;
        ident.jjtSetValue(id);


        Type type = (Type) node.jjtGetChild(0).jjtAccept(this,SCOPE);

        Node entetes = node.jjtGetChild(2);
        entetes.jjtAccept(this,SCOPE);
        Node vars = node.jjtGetChild(3);
        Node instrs = node.jjtGetChild(4);

        Object[] methComponents = new Object[3];
        methComponents[0] = entetes;
        methComponents[1] = vars;
        methComponents[2] = instrs;

        Quad quad;
        try {
            quad = new Quad(id,methComponents,type,Nature.METH);
        } catch (TypeException e) {
            throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",id,arrayToString(methComponents),type,Nature.METH),node.getLine(),node.getColumn());
        }
        try {
            symbolTable.put(quad);
        } catch (SymbolException e) {
            throw new TypeCheckerException("The symbol \"" + id + "\" already exists.",node.getLine(),node.getColumn());
        }

        node.jjtGetChild(3).jjtAccept(this,SCOPE);
        node.jjtGetChild(4).jjtAccept(this,SCOPE);

        if(!type.equals(Type.VOID) && !allReturnStatementNeeded(instrs)){
            throw new TypeCheckerException("Missing return statement",node.getLine(),node.getColumn());
        }
        if(!type.equals(Type.VOID)){
            boolean badType = false;
            List<Type> tmp = new ArrayList<>();
            validReturnType(tmp, instrs,SCOPE);
            for(Type t : tmp){
                if (!t.equals(type)) {
                    badType = true;
                    break;
                }
            }
            if(badType){
                throw new TypeCheckerException("Bad or missing return statement",node.getLine(),node.getColumn());
            }
        }else{
            if(instrs instanceof ASTinil){
                return null;
            }
            Type badType = null;
            List<Type> tmp = new ArrayList<>();
            validReturnType(tmp, instrs,SCOPE);
            for(Type t : tmp){
                if(!t.equals(Type.VOID)){
                    badType = t;
                }
            }
            if(badType != null){
                if(badType.equals(Type.BOOLEAN)){
                    throw new TypeCheckerException("Type VOID methode cannot return BOOLEAN",node.getLine(),node.getColumn());
                }
                if(badType.equals(Type.INTEGER)){
                    throw new TypeCheckerException("Type VOID methode cannot return INTEGER",node.getLine(),node.getColumn());
                }
            }
        }


        return null;
    }

    @Override
    public Object visit(ASTvars node, Object data) throws VisitorException {
        final String SCOPE = (String) data;
        node.jjtGetChild(0).jjtAccept(this, SCOPE);
        node.jjtGetChild(1).jjtAccept(this, SCOPE);
        return null;
    }

    @Override
    public Object visit(ASTvar node, Object data) throws VisitorException {
        final String SCOPE = (String) data;
        Node type = node.jjtGetChild(0);
        SimpleNode ident = (SimpleNode) node.jjtGetChild(1);
        Node exp = node.jjtGetChild(2);


        if(exp.jjtGetNumChildren()>0 && exp.jjtGetChild(0) instanceof ASTident){
            String idExp = (String) ((SimpleNode) exp.jjtGetChild(0)).jjtGetValue();

            Quad quadExp;
            try {
                quadExp = symbolTable.get(idExp + TypeChecker.SEPARATOR + data);
            } catch (SymbolException e) {
                try{
                    quadExp = symbolTable.get(idExp + TypeChecker.SEPARATOR + TypeChecker.GLOBAL);
                }catch (SymbolException ebis){
                    throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",idExp),node.getLine(),node.getColumn());
                }
            }

            if(quadExp.getNature().equals(Nature.TAB)){
                throw new TypeCheckerException("An array can't be affected to a variable",node.getLine(),node.getColumn());
            }
        }

        String id = (String) ident.jjtGetValue();
        Type exprType = (Type) exp.jjtAccept(this,data);
        Type expectedType = (Type) type.jjtAccept(this,data);

        if(expectedType.equals(Type.VOID)){
            throw new TypeCheckerException(String.format("Variable cannot be of type %s",type),node.getLine(),node.getColumn());
        }

        if(!exprType.equals(Type.OMEGA) && !exprType.equals(expectedType)){
            throw new TypeCheckerException(String.format("Value of type %s is not compatible with the variable %s of type %s",exprType,id,expectedType),node.getLine(),node.getColumn());
        }
        Quad quad;
        id += TypeChecker.SEPARATOR + SCOPE;
        try {
            quad = new Quad(id,null,expectedType, Nature.VAR);
        } catch (TypeException e) {
            throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",id,null,expectedType,Nature.VAR),node.getLine(),node.getColumn());
        }
        try {
            symbolTable.put(quad);
            ident.jjtSetValue(id);
        } catch (SymbolException e) {
            throw new TypeCheckerException("The symbol \"" + id + "\" already exists.",node.getLine(),node.getColumn());
        }
        return null;
    }

    @Override
    public Object visit(ASTvexp node, Object data) throws VisitorException {
        return node.jjtGetChild(0).jjtAccept(this,data);
    }

    @Override
    public Object visit(ASTomega node, Object data) {
        return Type.OMEGA;
    }

    @Override
    public Object visit(ASTmain node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, TypeChecker.MAIN);
        node.jjtGetChild(1).jjtAccept(this, TypeChecker.MAIN);
        return null;
    }

    @Override
    public Object visit(ASTentetes node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this,data);
        node.jjtGetChild(1).jjtAccept(this,data);
        return data;
    }

    @Override
    public Object visit(ASTenil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTentete node, Object data) throws VisitorException {
        System.out.println("entetes : "+data);
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);

        if(data == null){
            return t1;
        }

        String id = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        String ident = id + TypeChecker.SEPARATOR + data;
        ((SimpleNode) node.jjtGetChild(1)).jjtSetValue(ident);

        Quad quad;

        try {
            quad = new Quad(ident,null,t1,Nature.VAR);
        } catch (TypeException e) {
            throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",ident,null,t1,Nature.VAR),node.getLine(),node.getColumn());
        }

        try {
            symbolTable.get(ident);
        } catch (SymbolException e) {
            try {
                symbolTable.put(quad);
            } catch (SymbolException ex) {
                throw new TypeCheckerException(String.format("Invalid declaration Quad(%s,%s,%s,%s)",ident,null,t1,Nature.VAR),node.getLine(),node.getColumn());
            }
        }

        return t1;
    }

    @Override
    public Object visit(ASTinstrs node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTinil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTinstr node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this, data);
        if(node.jjtGetNumChildren() > 1){
            node.jjtGetChild(1).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTretour node, Object data) throws VisitorException {
        if(node.jjtGetChild(0) instanceof ASTappelE){
            System.out.println(node.jjtGetChild(0));
        }
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        if(t1.equals(Type.VOID)){
            throw new TypeCheckerException("You cannot return an element of type VOID",node.getLine(),node.getColumn());
        }
        return t1;
    }

    @Override
    public Object visit(ASTecrire node, Object data) throws VisitorException {
        if(node.jjtGetChild(0) instanceof ASTchaine){
            return null;
        }else{
            ASTident ident = (ASTident) node.jjtGetChild(0);
            ident.jjtAccept(this,data);
            //ident.jjtSetValue(ident.jjtGetValue() + TypeChecker.SEPARATOR + data);
        }
        return null;
    }

    @Override
    public Object visit(ASTecrireln node, Object data) throws VisitorException {
        if(node.jjtGetChild(0) instanceof ASTchaine){
            return null;
        }else{
            ASTident ident = (ASTident) node.jjtGetChild(0);
            ident.jjtAccept(this,data);
            //ident.jjtSetValue(ident.jjtGetValue() + TypeChecker.SEPARATOR + data);
        }
        return null;
    }

    @Override
    public Object visit(ASTsi node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        node.jjtGetChild(1).jjtAccept(this,data);
        if(node.jjtGetNumChildren() > 2){
            node.jjtGetChild(2).jjtAccept(this,data);
        }

        if(!t1.equals(Type.BOOLEAN)){
            throw new TypeCheckerException(String.format("Bad operand type, if(%s) is not valid, must be a boolean",t1),node.getLine(),node.getColumn());
        }

        return null;
    }

    @Override
    public Object visit(ASTtantque node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.BOOLEAN)){
            throw new TypeCheckerException(String.format("Bad operand type, while(%s) is not valid, must be a boolean",t1),node.getLine(),node.getColumn());
        }

        return null;
    }

    @Override
    public Object visit(ASTappelI node, Object data) throws VisitorException {
        SimpleNode ident = (SimpleNode) node.jjtGetChild(0);

        String id = ident.jjtGetValue() + TypeChecker.SEPARATOR + callHeaderToString(node.jjtGetChild(1), (String) ident.jjtGetValue(),data);
        ident.jjtSetValue(id);

        Quad quad;
        try {
            quad = symbolTable.get(id);
        } catch (SymbolException e) {
            throw new TypeCheckerException("The symbol \"" + id + "\" doesn't exists.",node.getLine(),node.getColumn());
        }

        /*if(!quad.getType().equals(Type.VOID)){
            throw new TypeCheckerException(String.format("(%s,%s) VOID type methode cannot return something",node.getLine(),node.getColumn()));
        }*/

        return quad.getType();
    }

    @Override
    public Object visit(ASTtab node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);
        if(!t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, array[%s] is not valid, must be an integer",t2),node.getLine(),node.getColumn());
        }

        return t1;
    }

    @Override
    public Object visit(ASTaffectation node, Object data) throws VisitorException {
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);
        String id;
        if(node.jjtGetChild(0) instanceof ASTtab){
            node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this,data);
            id = (String) ((SimpleNode) node.jjtGetChild(0).jjtGetChild(0)).jjtGetValue();
            if(node.jjtGetChild(0).jjtGetChild(1) instanceof ASTident){
                node.jjtGetChild(0).jjtGetChild(1).jjtAccept(this,data);
            }
        }else{
            node.jjtGetChild(0).jjtAccept(this,data);
            id = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        }

        Quad quad;
        try {
            quad = symbolTable.get(id);
        } catch (SymbolException e) {
            try{
                quad = symbolTable.get(id.split(TypeChecker.SEPARATOR)[0]+TypeChecker.SEPARATOR+TypeChecker.GLOBAL);
                if(node.jjtGetChild(0) instanceof ASTtab){
                    ((SimpleNode) node.jjtGetChild(0).jjtGetChild(0)).jjtSetValue(id.split(TypeChecker.SEPARATOR)[0]+TypeChecker.SEPARATOR+TypeChecker.GLOBAL);
                }else{
                    ((SimpleNode) node.jjtGetChild(0)).jjtSetValue(id.split(TypeChecker.SEPARATOR)[0]+TypeChecker.SEPARATOR+TypeChecker.GLOBAL);
                }
            }catch (SymbolException ebis){
                throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",id),node.getLine(),node.getColumn());
            }
        }

        if(node.jjtGetChild(1) instanceof ASTident){
            String id2 = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();

            Quad quad2;
            try {
                quad2 = symbolTable.get(id2);
            } catch (SymbolException e) {
                try{
                    quad2 = symbolTable.get(id2.split(TypeChecker.SEPARATOR)[0]+TypeChecker.SEPARATOR + TypeChecker.GLOBAL);
                }catch (SymbolException ebis){
                    throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",id2),node.getLine(),node.getColumn());
                }
            }

            //&& (node.jjtGetChild(0) instanceof ASTtab || node.jjtGetChild(0) instanceof ASTident)){
            /*if(quad.getNature().equals(Nature.TAB) && node.jjtGetChild(0) instanceof ASTident && !(node.jjtGetChild(0) instanceof ASTident) && ){
                throw new TypeCheckerException("Only array can be affected to an other array",node.getLine(),node.getColumn());
            }

            if(quad2.getNature().equals(Nature.TAB) && !quad.getNature().equals(quad2.getNature())){
                throw new TypeCheckerException("An array can't be affected to a variable",node.getLine(),node.getColumn());
            }*/

            if(quad.getNature().equals(Nature.TAB)){
                if(node.jjtGetChild(0) instanceof ASTident){
                    if(!quad2.getNature().equals(Nature.TAB) || !(node.jjtGetChild(0) instanceof ASTident)){
                        throw new TypeCheckerException("Only array can be affected to an other array",node.getLine(),node.getColumn());
                    }
                }else{
                    if(quad2.getNature().equals(Nature.TAB) && node.jjtGetChild(1) instanceof ASTident){
                        throw new TypeCheckerException("An array can't be affected to a variable",node.getLine(),node.getColumn());
                    }
                }
            }else{
                if(quad2.getNature().equals(Nature.TAB) && node.jjtGetChild(1) instanceof ASTident){
                    throw new TypeCheckerException("An array can't be affected to a variable",node.getLine(),node.getColumn());
                }
            }
        }

        if(node.jjtGetChild(1) instanceof ASTtab && quad.getNature().equals(Nature.TAB) && !(node.jjtGetChild(0) instanceof ASTtab)){
            throw new TypeCheckerException("Only array can be affected to an other array",node.getLine(),node.getColumn());
        }


        if(!quad.getType().equals(t2)){
            throw new TypeCheckerException(String.format("Bad operand type, %s = %s is not valid, must be same type",quad.getType(),t2),node.getLine(),node.getColumn());
        }

        return quad.getType();
    }

    @Override
    public Object visit(ASTsomme node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s += %s is not valid, must be integers",t1,t2),node.getLine(),node.getColumn());
        }

        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTincrement node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s++ is not valid, must be integer",t1),node.getLine(),node.getColumn());
        }

        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTlistexp node, Object data) throws VisitorException {
        node.jjtGetChild(0).jjtAccept(this,data);
        if(node.jjtGetNumChildren()>1){
            node.jjtGetChild(1).jjtAccept(this,data);
        }
        return null;
    }

    @Override
    public Object visit(ASTexnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTnon node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        if(!t1.equals(Type.BOOLEAN)){
            throw new TypeCheckerException(String.format("Bad operand type, !%s is not a valid operation, must be a boolean",t1),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTneg node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        if(!t1.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, -%s is not a valid operation, must be a boolean",t1),node.getLine(),node.getColumn());
        }
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTet node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.BOOLEAN) || !t2.equals(Type.BOOLEAN)){
            throw new TypeCheckerException(String.format("Bad operand type, %s and %s is not a valid operation, must be a boolean",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTou node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.BOOLEAN) || !t2.equals(Type.BOOLEAN)){
            throw new TypeCheckerException(String.format("Bad operand type, %s or %s is not a valid operation, must be a boolean",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTegal node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);
        if(!t1.equals(t2)){
            throw new TypeCheckerException(String.format("Bad operand type, %s == %s is not a valid operation, must be same type",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTinf node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s < %s is not a valid operation, must be integers",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTsup node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s > %s is not a valid operation, must be integers",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTplus node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s + %s is not a valid operation",t1,t2),node.getLine(),node.getColumn());
        }
        return t1;
    }

    @Override
    public Object visit(ASTmoins node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s - %s is not a valid operation",t1,t2),node.getLine(),node.getColumn());
        }
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTmult node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s * %s is not a valid operation",t1,t2),node.getLine(),node.getColumn());
        }
        return t1;
    }

    @Override
    public Object visit(ASTdiv node, Object data) throws VisitorException {
        Type t1 = (Type) node.jjtGetChild(0).jjtAccept(this,data);
        Type t2 = (Type) node.jjtGetChild(1).jjtAccept(this,data);

        if(!t1.equals(Type.INTEGER) || !t2.equals(Type.INTEGER)){
            throw new TypeCheckerException(String.format("Bad operand type, %s / %s is not a valid operation",t1,t2),node.getLine(),node.getColumn());
        }
        return t1;
    }

    @Override
    public Object visit(ASTlongueur node, Object data) {
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTvrai node, Object data) {
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTfaux node, Object data) {
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTappelE node, Object data) throws VisitorException {
        SimpleNode ident = (SimpleNode) node.jjtGetChild(0);
        String tmp = (String) ident.jjtGetValue();

        String id = tmp;
        if(!tmp.contains(TypeChecker.SEPARATOR)){
            String scope = callHeaderToString(node.jjtGetChild(1),(String) ident.jjtGetValue(),data);
            id += TypeChecker.SEPARATOR + scope;
            ident.jjtSetValue(id);
        }

        Quad quad;
        try {
            quad = symbolTable.get(id);
        } catch (SymbolException e) {
            throw new TypeCheckerException(String.format("The symbol \"%s\" doesn't exists.",id),node.getLine(),node.getColumn());
        }

        /*if(quad.getType().equals(Type.VOID)){
            throw new TypeCheckerException(String.format("(%s,%s) A method of type void cannot return something",node.getLine(),node.getColumn()));
        }*/

        return quad.getType();
    }

    @Override
    public Object visit(ASTnbre node, Object data) {
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTrien node, Object data) {
        return Type.VOID;
    }

    @Override
    public Object visit(ASTentier node, Object data) {
        return Type.INTEGER;
    }

    @Override
    public Object visit(ASTbooleen node, Object data) {
        return Type.BOOLEAN;
    }

    @Override
    public Object visit(ASTchaine node, Object data) {
        return Type.OMEGA;
    }

    private boolean allReturnStatementNeeded(Node instrs){
        boolean allNeeded = false;
        while(instrs instanceof ASTinstrs){
            Node instr = instrs.jjtGetChild(0);

            if(instr instanceof ASTsi){
                if(instrs.jjtGetChild(0).jjtGetNumChildren() <= 2){
                    allNeeded = allReturnStatementNeeded(instr.jjtGetChild(1));
                }else{
                    allNeeded = allReturnStatementNeeded(instr.jjtGetChild(1)) && allReturnStatementNeeded(instr.jjtGetChild(2));
                }
            }

            if(instr instanceof ASTtantque){
                allNeeded = allReturnStatementNeeded(instrs.jjtGetChild(1));
            }

            if(instr instanceof ASTretour){
                return true;
            }
            instrs = instrs.jjtGetChild(1);
        }
        return allNeeded;
    }

    public List<Type> validReturnType(List<Type> types, Node instrs, Object data) throws VisitorException {
        while(instrs instanceof ASTinstrs){
            Node instr = instrs.jjtGetChild(0);
            if(instr instanceof ASTsi){
                if(instr.jjtGetNumChildren() <= 2){
                    types.addAll(validReturnType(types,instr.jjtGetChild(1),data));
                }else{
                    types.addAll(validReturnType(types,instr.jjtGetChild(1),data));
                    types.addAll(validReturnType(types,instr.jjtGetChild(2),data));
                }
            }

            if(instr instanceof ASTtantque){
                types.addAll(validReturnType(types,instr.jjtGetChild(1),data));
            }
            if(instr instanceof ASTretour){
                types.add((Type) instrs.jjtGetChild(0).jjtAccept(this, data));
            }
            instrs = instrs.jjtGetChild(1);
        }
        return types;
    }

    private String callHeaderToString(Node listExp, String name, Object data) throws VisitorException {
        StringBuilder res = new StringBuilder(name+":");
        Type currType;

        if(listExp instanceof ASTexnil){
            return res.toString();
        }


        while (!(listExp.jjtGetChild(1) instanceof ASTexnil)){
            currType = (Type) listExp.jjtGetChild(0).jjtAccept(this,data);
            res.append(currType);
            if(!(listExp.jjtGetChild(1) instanceof ASTenil)){
                res.append(TypeChecker.PARAM_SEPARATOR);
            }
            listExp = (ASTlistexp) listExp.jjtGetChild(1);
        }
        currType = (Type) listExp.jjtGetChild(0).jjtAccept(this,data);
        res.append(currType);
        return  res.toString();
    }

    private String headerToString(ASTentetes headers,String name, Object data) throws VisitorException {
        StringBuilder res = new StringBuilder(name+":");
        Type currType;

        while (!(headers.jjtGetChild(1) instanceof ASTenil)){
            currType = (Type) headers.jjtGetChild(0).jjtAccept(this,data);
            res.append(currType);
            if(!(headers.jjtGetChild(1) instanceof ASTenil)){
                res.append(TypeChecker.PARAM_SEPARATOR);
            }
            headers = (ASTentetes) headers.jjtGetChild(1);
        }
        currType = (Type) headers.jjtGetChild(0).jjtAccept(this,data);
        res.append(currType);

        return  res.toString();
    }

    private String arrayToString(Object[] array){
        StringBuilder res = new StringBuilder(" [");
        for(int i = 0, l = array.length; i < l ; ++i){
            res.append(array[i].getClass().getSimpleName());
            if(i < l-1){
                res.append(", ");
            }
        }
        return res.append("]").toString();
    }
}
