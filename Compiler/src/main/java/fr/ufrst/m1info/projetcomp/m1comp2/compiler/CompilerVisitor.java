package fr.ufrst.m1info.projetcomp.m1comp2.compiler;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.*;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.ASTStart;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.Node;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.SimpleNode;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.*;

import java.util.ArrayList;

import static fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.JajaCodeTreeConstants.*;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.JJTIDENT;
import static fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.MiniJajaTreeConstants.JJTNBRE;

public class CompilerVisitor implements MiniJajaVisitor {

    ArrayList<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> listInstr = new ArrayList<>();

    public ArrayList<fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node> getListInstr() {
        return listInstr;
    }

    public void clearListInstr() {
        listInstr.clear();
    }

    @Override
    public Object visit(SimpleNode node, Object data) throws VisitorException {
        node.childrenAccept(this, data);
        return 0;
    }

    @Override
    public Object visit(ASTStart node, Object data) throws VisitorException {
        node.childrenAccept(this, data);
        return 0;
    }

    @Override
    public Object visit(ASTclasse node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodeInit = new ASTInit(JJTINIT);
        var nodePop = new ASTPop(JJTPOP);
        var nodeJcstop = new ASTJCStop(JJTJCSTOP);

        listInstr.add(nodeInit);

        var ndss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + 1, CompilerMode.DEFAULT));
        var nmma = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerData(n + ndss + 1, CompilerMode.DEFAULT));
        var nrdss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ndss + nmma + 1, CompilerMode.REMOVE));

        listInstr.add(nodePop);
        listInstr.add(nodeJcstop);

        return ndss+nmma+nrdss+3;
    }

    @Override
    public Object visit(ASTident node, Object data) {
        var nodeLoad = new ASTLoad(JJTLOAD);
        var ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue(node.jjtGetValue());
        nodeLoad.jjtAddChild(ident,0);
        listInstr.add(nodeLoad);

        return 1;
    }

    @Override
    public Object visit(ASTdecls node, Object data) throws VisitorException {
        return compileVarDeclaration(node, data);
    }

    @Override
    public Object visit(ASTvnil node, Object data) {
        return 0;
    }

    private ASTident makeIdent(String ident) {
        var nodeIdent = new ASTident(JJTIDENT);
        nodeIdent.jjtSetValue(ident);
        return nodeIdent;
    }

    @Override
    public Object visit(ASTcst node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (mode == CompilerMode.DEFAULT) {
            var nodeNew = createNodeNew(node, 0, true);

            var ne = (int) node.jjtGetChild(2).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

            listInstr.add(nodeNew);

            return ne + 1;
        }
        if (mode == CompilerMode.REMOVE) {
            var nodeSwap = new ASTSwap(JJTSWAP);
            var nodePop = new ASTPop(JJTPOP);
            listInstr.add(nodeSwap);
            listInstr.add(nodePop);
            return 2;
        }

        return 0;
    }

    @Override
    public Object visit(ASTtableau node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var mode = (CompilerMode) cdata.args[1];


        if(mode == CompilerMode.DEFAULT){
            var nodeNewArray = new ASTNewArray(7);
            var ne = (int) node.jjtGetChild(2).jjtAccept(this, data);
            listInstr.add(nodeNewArray);
            var nodeIdent = new ASTJCIdent(JJTJCIDENT);
            nodeIdent.jjtSetValue(((ASTident) node.jjtGetChild(1)).jjtGetValue());

            var nodeType = new ASTJCType(JJTJCTYPE);
            nodeType.jjtSetValue(getTypeFromNode(node.jjtGetChild(0)));

            nodeNewArray.jjtAddChild(nodeIdent, 0);
            nodeNewArray.jjtAddChild(nodeType, 1);
            return ne+1;
        }
        if(mode == CompilerMode.REMOVE){
            var nodeSwap = new ASTSwap(5);
            var nodePop = new ASTPop(13);
            listInstr.add(nodeSwap);
            listInstr.add(nodePop);
            return 2;
        }
        return 0;
    }

    @Override
    public Object visit(ASTmethode node, Object data) throws VisitorException {
        var nodeTypeMjj  = node.jjtGetChild(0);
        var nodeIdentMjj = (ASTident) node.jjtGetChild(1);
        var nodeHeaders  = node.jjtGetChild(2);
        var nodeVars     = node.jjtGetChild(3);
        var nodeInstrs   = node.jjtGetChild(4);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        var typeIsVoid = nodeTypeMjj instanceof ASTrien;
        var nr = typeIsVoid ? 6 : 5;
        var nh = getHeadersNumber(nodeHeaders);

        if (mode == CompilerMode.DEFAULT) {
            var nodePush   = new ASTPush(JJTPUSH);
            var nodeNew    = new ASTNew(JJTNEW);
            var nodeGoto   = new ASTGoto(JJTGOTO);
            var nodeSwap   = new ASTSwap(JJTSWAP);
            var nodeReturn = new ASTReturn(JJTRETURN);

            var nodeNbre = new ASTJCNbre(JJTJCNBRE);
            nodeNbre.jjtSetValue(n + 3);
            nodePush.jjtAddChild(nodeNbre, 0);

            listInstr.add(nodePush);

            var nodeIdent = new ASTJCIdent(JJTJCIDENT);
            nodeIdent.jjtSetValue(nodeIdentMjj.jjtGetValue());

            var nodeType = new ASTJCType(JJTJCTYPE);
            nodeType.jjtSetValue(getTypeFromNode(nodeTypeMjj));

            var nodeNature = new ASTJCSorte(JJTJCSORTE);
            nodeNature.jjtSetValue("meth");

            var nodeNb = new ASTJCNbre(JJTJCNBRE);
            nodeNb.jjtSetValue(0);

            nodeNew.jjtAddChild(nodeIdent, 0);
            nodeNew.jjtAddChild(nodeType, 1);
            nodeNew.jjtAddChild(nodeNature, 2);
            nodeNew.jjtAddChild(nodeNb, 3);

            listInstr.add(nodeNew);
            listInstr.add(nodeGoto);

            var nens = (int) nodeHeaders.jjtAccept(this, new CompilerData(n + 3, CompilerMode.DEFAULT, nh));
            var ndvs = (int)  nodeVars.jjtAccept(this, new CompilerData(n + nens + 3, CompilerMode.DEFAULT));

            var niss = (int) nodeInstrs.jjtAccept(this, new CompilerData(n + nens + ndvs + 3, CompilerMode.DEFAULT));

            // Handling returns
            if (typeIsVoid) {
                var push0 = new ASTPush(JJTPUSH);
                var zero = new ASTJCNbre(JJTJCNBRE);

                zero.jjtSetValue(0);
                push0.jjtAddChild(zero, 0);

                listInstr.add(push0);
            }

            var nrdvs = (int) nodeVars.jjtAccept(this, new CompilerData(n + nens + ndvs + 3, CompilerMode.REMOVE));
            //var nrens = (int) nodeHeaders.jjtAccept(this, new CompilerData(n + nens + ndvs + nrdvs + 3, CompilerMode.REMOVE));

            var goToAdr = new ASTJCNbre(JJTJCNBRE);
            goToAdr.jjtSetValue(n + nens + ndvs + niss + nrdvs + nr);
            nodeGoto.jjtAddChild(goToAdr, 0);

            listInstr.add(nodeSwap);
            listInstr.add(nodeReturn);

            return nens + ndvs + niss + nrdvs + nr;
        }
        if (mode == CompilerMode.REMOVE) {
            return removeDeclaration();
        }

        return 0;
    }

    @Override
    public Object visit(ASTvar node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var mode = (CompilerMode) cdata.args[1];
        var n = (Integer) cdata.args[0];

        if (mode == CompilerMode.DEFAULT) {
            var nodeNew = createNodeNew(node, 0, false);
            var ne = (int) node.jjtGetChild(2).jjtAccept(this, data);
            listInstr.add(nodeNew);
            return ne + 1;
        }
        if (mode == CompilerMode.REMOVE) {
            return removeDeclaration();
        }

        return 0;
    }

    @Override
    public Object visit(ASTvars node, Object data) throws VisitorException {
        return compileVarDeclaration(node, data);
    }

    @Override
    public Object visit(ASTvexp node, Object data) throws VisitorException {
        /*var nodePush = new ASTPush(JJTPUSH);

        var str = new ASTJCString(JJTJCNBRE);
        str.jjtSetValue("OMEGA");
        nodePush.jjtAddChild(str,0);

        listInstr.add(nodePush);*/
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTomega node, Object data) {
        var nodePush = new ASTPush(JJTPUSH);

        var str = new ASTJCString(JJTJCNBRE);
        str.jjtSetValue("OMEGA");
        nodePush.jjtAddChild(str,0);

        listInstr.add(nodePush);

        return 1;
    }

    @Override
    public Object visit(ASTmain node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodePush = new ASTPush(JJTPUSH);

        //creating node for push(nbre)
        var nbre = new ASTJCNbre(JJTNBRE);
        nbre.jjtSetValue(0);
        nodePush.jjtAddChild(nbre,0);

        var ndvs = (int) node.jjtGetChild(0).jjtAccept(this, data);
        var niss = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ndvs, CompilerMode.DEFAULT));
        listInstr.add(nodePush);
        var nrdvs = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n + ndvs + niss + 1, CompilerMode.REMOVE));

        return ndvs + niss + nrdvs + 1;
    }

    @Override
    public Object visit(ASTentetes node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];
        int nh=-1;
        if (cdata.args.length == 3)
            nh = (Integer) cdata.args[2];

        if (mode == CompilerMode.DEFAULT) {
            var nodeHeader = node.jjtGetChild(0);
            var nodeHeaders = node.jjtGetChild(1);

            var nens = (int) nodeHeaders.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT, nh - 1));
            var nen = (int) nodeHeader.jjtAccept(this, new CompilerData(n + nens, CompilerMode.DEFAULT, nh));



            return nens + nen;
        }

        if (mode == CompilerMode.REMOVE) {
            var nodeHeader = node.jjtGetChild(0);
            var nodeHeaders = node.jjtGetChild(1);

            var nrens = (int) nodeHeaders.jjtAccept(this, new CompilerData(n, CompilerMode.REMOVE));
            var nren = (int) nodeHeader.jjtAccept(this, new CompilerData(n + nrens, CompilerMode.REMOVE));


            return nrens + nren;
        }

        return 0;
    }

    @Override
    public Object visit(ASTenil node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTentete node, Object data) {
        var cdata = (CompilerData) data;
        int s=-1;
        if (cdata.args.length == 3)
            s = (Integer) cdata.args[2];
        var mode = (CompilerMode) cdata.args[1];
        if (mode == CompilerMode.DEFAULT) {
            listInstr.add(createNodeNew(node, s, false));
            return 1;
        }
        if (mode == CompilerMode.REMOVE) {
            removeDeclaration();
            return 2;
        }

        return 0;
    }

    @Override
    public Object visit(ASTinstrs node, Object data) throws VisitorException {
        var nis = (int) node.jjtGetChild(0).jjtAccept(this, data);
        var niss = (int) node.jjtGetChild(1).jjtAccept(this, data);
        return nis + niss;
    }

    @Override
    public Object visit(ASTinil node, Object data) {
        return 0;
    }

    //TODO le node instr n'est peut etre pas bon
    @Override
    public Object visit(ASTinstr node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTretour node, Object data) throws VisitorException {
        var ne = (int) node.jjtGetChild(0).jjtAccept(this, data);
        return ne ;
    }

    @Override
    public Object visit(ASTecrire node, Object data) throws VisitorException {
        var nodeWrite = new ASTWrite(JJTWRITE);
        var ne = (int) node.jjtGetChild(0).jjtAccept(this, data);
        listInstr.add(nodeWrite);

        return ne + 1;
    }

    @Override
    public Object visit(ASTecrireln node, Object data) throws VisitorException {
        var nodeWriteln = new ASTWriteln(JJTWRITELN);

        var ne =(int) node.jjtGetChild(0).jjtAccept(this,data);
        listInstr.add(nodeWriteln);



        return ne+1;
    }

    @Override
    public Object visit(ASTsi node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var condition = node.jjtGetChild(0);
        var issIf = node.jjtGetChild(1);
        var issElse = node.jjtGetChild(2);

        var nodeIf = new ASTIf(JJTIF);
        var nodeGoto = new ASTGoto(JJTGOTO);

        var ne = (int) condition.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

        listInstr.add(nodeIf);

        var niss2 = (int) issElse.jjtAccept(this, new CompilerData(n + ne + 1, CompilerMode.DEFAULT));

        var nodeAddressIf = new ASTJCNbre(JJTJCNBRE);
        nodeAddressIf.jjtSetValue(n + ne + niss2 + 2);
        nodeIf.jjtAddChild(nodeAddressIf, 0);

        listInstr.add(nodeGoto);

        var niss1 = (int) issIf.jjtAccept(this, new CompilerData(n + ne + niss2 + 2, CompilerMode.DEFAULT));

        var nodeAddressGoTo = new ASTJCNbre(JJTJCNBRE);
        nodeAddressGoTo.jjtSetValue(n + ne + niss2 + niss1 + 2);
        nodeGoto.jjtAddChild(nodeAddressGoTo, 0);

        return ne + niss1 + niss2 + 2;
    }

    @Override
    public Object visit(ASTtantque node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var ne = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

        var nodeNot = new ASTNot(JJTNOT);
        listInstr.add(nodeNot);
        var nodeIf = new ASTIf(JJTIF);
        listInstr.add(nodeIf);

        var nis = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ne + 2, CompilerMode.DEFAULT));

        var nodeAddressIf = new ASTJCNbre(JJTJCNBRE);
        nodeAddressIf.jjtSetValue(n + ne + nis + 3);
        nodeIf.jjtAddChild(nodeAddressIf, 0);

        var nodeGoto = new ASTGoto(JJTGOTO);
        var nodeAddressGoTo = new ASTJCNbre(JJTJCNBRE);
        nodeAddressGoTo.jjtSetValue(n);
        nodeGoto.jjtAddChild(nodeAddressGoTo, 0);
        listInstr.add(nodeGoto);

        return ne + nis + 3;
    }

    @Override
    public Object visit(ASTappelI node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodeIdentMjj = (ASTident) node.jjtGetChild(0);
        var nodeListexp = node.jjtGetChild(1);

        var nodeInvoke = new ASTInvoke(JJTINVOKE);
        var nodeIdent = new ASTJCIdent(JJTJCIDENT);
        nodeIdent.jjtSetValue(nodeIdentMjj.jjtGetValue());
        nodeInvoke.jjtAddChild(nodeIdent, 0);

        var nodePop = new ASTPop(JJTPOP);

        var nlexp = (int) nodeListexp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

        listInstr.add(nodeInvoke);

        var rnlexp = (int) nodeListexp.jjtAccept(this, new CompilerData(n, CompilerMode.REMOVE));

        listInstr.add(nodePop);

        return  nlexp + rnlexp + 2;
    }


    @Override
    public Object visit(ASTappelE node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodeIdentMjj = (ASTident) node.jjtGetChild(0);
        var nodeListexp = node.jjtGetChild(1);

        var nodeInvoke = new ASTInvoke(JJTINVOKE);
        var nodeIdent = new ASTJCIdent(JJTJCIDENT);
        nodeIdent.jjtSetValue(nodeIdentMjj.jjtGetValue());
        nodeInvoke.jjtAddChild(nodeIdent, 0);

        var nlexp = (int) nodeListexp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

        listInstr.add(nodeInvoke);

        var rnlexp = (int) nodeListexp.jjtAccept(this, new CompilerData(n, CompilerMode.REMOVE));

        return  nlexp + rnlexp + 1;
    }

    @Override
    public Object visit(ASTtab node, Object data) throws VisitorException {
        var nodeExp = node.jjtGetChild(1);
        var nodeIdentMjj = (ASTident) node.jjtGetChild(0);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (mode == CompilerMode.DEFAULT) {
            var nodeAload = new ASTALoad(JJTALOAD);
            var nodeIdent = new ASTJCIdent(JJTJCIDENT);

            nodeIdent.jjtSetValue(nodeIdentMjj.jjtGetValue());
            nodeAload.jjtAddChild(nodeIdent, 0);

            var ne = (int) nodeExp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

            listInstr.add(nodeAload);

            return ne + 1;
        }

        return 0;
    }

    @Override
    public Object visit(ASTaffectation node, Object data) throws VisitorException {
        var nodeIdent = node.jjtGetChild(0);
        var nodeExp = node.jjtGetChild(1);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (nodeIdent instanceof ASTtab) {
            var nodeIndex = nodeIdent.jjtGetChild(1);

            if (mode == CompilerMode.DEFAULT) {
                var nodeAStore = new ASTAStore(JJTASTORE);
                var nodeTab = (ASTtab) nodeIdent;
                var identAStore = new ASTJCIdent(JJTJCIDENT);

                var i = (ASTident) nodeTab.jjtGetChild(0);
                identAStore.jjtSetValue(i.jjtGetValue());
                nodeAStore.jjtAddChild(identAStore, 0);

                var ne = (int) nodeIndex.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
                var ne1 = (int) nodeExp.jjtAccept(this, new CompilerData(n + ne, CompilerMode.DEFAULT));

                listInstr.add(nodeAStore);

                return ne + ne1 + 1;
            }

            return 0;
        }

        if (mode == CompilerMode.DEFAULT) {
            var nodeStore = new ASTStore(JJTSTORE);

            var ident = new ASTJCIdent(JJTJCIDENT);
            ident.jjtSetValue(((ASTident) node.jjtGetChild(0)).jjtGetValue());
            nodeStore.jjtAddChild(ident, 0);

            var ne = (int) nodeExp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
            listInstr.add(nodeStore);

            return ne + 1;
        }

        return 0;
    }

    @Override
    public Object visit(ASTsomme node, Object data) throws VisitorException {
        var nodeIdent = node.jjtGetChild(0);
        var nodeExp = node.jjtGetChild(1);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (nodeIdent instanceof ASTtab) {
            var nodeTab = (ASTtab) nodeIdent;
            var nodeIndex = nodeIdent.jjtGetChild(1);

            if (mode == CompilerMode.DEFAULT) {
                var nodeAinc = new ASTAInc(JJTAINC);

                var identAinc = new ASTJCIdent(JJTJCIDENT);
                var i = (ASTident) nodeTab.jjtGetChild(0);

                identAinc.jjtSetValue(i.jjtGetValue());
                nodeAinc.jjtAddChild(identAinc, 0);

                var ne = (int) nodeIndex.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
                var ne1 = (int) nodeExp.jjtAccept(this, new CompilerData(n + ne, CompilerMode.DEFAULT));

                listInstr.add(nodeAinc);

                return ne + ne1 + 1;
            }
        }

        if (mode == CompilerMode.DEFAULT) {
            var nodeInc = new ASTInc(JJTINC);
            var nodeIdentInc = new ASTJCIdent(JJTJCIDENT);
            nodeIdentInc.jjtSetValue(((ASTident) nodeIdent).jjtGetValue());
            nodeInc.jjtAddChild(nodeIdentInc, 0);
            var ne = (int) nodeExp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

            listInstr.add(nodeInc);

            return ne + 1;
        }

        return 0;
    }

    @Override
    public Object visit(ASTincrement node, Object data) throws VisitorException {
        var nodeIdent = node.jjtGetChild(0);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (nodeIdent instanceof ASTtab) {
            var nodeIndex = nodeIdent.jjtGetChild(1);

            if (mode == CompilerMode.DEFAULT) {
                var nodeAinc = new ASTAInc(JJTAINC);
                var nodePush = new ASTPush(JJTPUSH);

                var nodeTab = (ASTtab) nodeIdent;

                var identAinc = new ASTJCIdent(JJTJCIDENT);
                var i = (ASTident) nodeTab.jjtGetChild(0);
                identAinc.jjtSetValue(i.jjtGetValue());
                nodeAinc.jjtAddChild(identAinc, 0);

                var nodeNbre = new ASTJCNbre(JJTJCNBRE);
                nodeNbre.jjtSetValue(1);
                nodePush.jjtAddChild(nodeNbre, 0);

                var ne = (int) nodeIndex.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));

                listInstr.add(nodePush);
                listInstr.add(nodeAinc);

                return ne + 2;
            }

            return 0;
        }

        if (mode == CompilerMode.DEFAULT) {
            var nodePush = new ASTPush(JJTPUSH);

            //creating node for push(nbre)
            var nbre = new ASTJCNbre(JJTJCNBRE);
            nbre.jjtSetValue(1);
            nodePush.jjtAddChild(nbre, 0);

            var nodeInc = new ASTInc(JJTINC);

            //creating node for inc(ident)
            var ident = new ASTJCIdent(JJTJCNBRE);
            ident.jjtSetValue(((ASTident) node.jjtGetChild(0)).jjtGetValue());
            nodeInc.jjtAddChild(ident, 0);

            listInstr.add(nodePush);
            listInstr.add(nodeInc);

            return 2;
        }
        return 0;
    }

    @Override
    public Object visit(ASTlistexp node, Object data) throws VisitorException {
        var nodeExp = node.jjtGetChild(0);
        Node nodeListExp = null;
        if(node.jjtGetNumChildren() > 1){
            nodeListExp = node.jjtGetChild(1);
        }

        var cdata = (CompilerData) data;
        var mode = (CompilerMode) cdata.args[1];
        var n = (Integer) cdata.args[0];

        if (mode == CompilerMode.DEFAULT) {
            var nexp = (int) nodeExp.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
            var nlexp = 0;
            if(nodeListExp != null){
                nlexp = (int) nodeListExp.jjtAccept(this, new CompilerData(n + nexp, CompilerMode.DEFAULT));
            }
            return nexp + nlexp;

        }
        if (mode == CompilerMode.REMOVE && nodeListExp != null) {
            var nodeSwapR = new ASTSwap(JJTSWAP);
            var nodePopR = new ASTPop(JJTPOP);

            listInstr.add(nodeSwapR);
            listInstr.add(nodePopR);

            var nrlexp = (int) nodeListExp.jjtAccept(this, new CompilerData(n, CompilerMode.REMOVE));

            return nrlexp + 2;
        }

        return 0;
    }

    @Override
    public Object visit(ASTexnil node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTnon node, Object data) throws VisitorException {
        var nodeNot = new ASTNot(JJTNOT);
        var ne = (int) node.jjtGetChild(0).jjtAccept(this, data);

        listInstr.add(nodeNot);

        return ne + 1;
    }

    @Override
    public Object visit(ASTneg node, Object data) throws VisitorException {
        var nodeNeg = new ASTNeg(JajaCodeTreeConstants.JJTNEG);
        var ne = (int) node.jjtGetChild(0).jjtAccept(this, data);
        listInstr.add(nodeNeg);

        return ne + 1;
    }

    @Override
    public Object visit(ASTet node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodeIf = new ASTIf(JJTIF);
        var nodePush = new ASTPush(JJTPUSH);
        var nodeGoto = new ASTGoto(JJTGOTO);

        var ne1 = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
        listInstr.add(nodeIf);
        listInstr.add(nodeGoto);
        listInstr.add(nodePush);
        var ne2 = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ne1 + 3, CompilerMode.DEFAULT));

        var nodeAdresseIf = new ASTJCNbre(JJTJCNBRE);
        nodeAdresseIf.jjtSetValue(n + ne1 + 3);
        nodeIf.jjtAddChild(nodeAdresseIf, 0);

        var nodeFalse = new ASTJCFaux(JJTJCFAUX);
        nodeFalse.jjtSetValue(false);
        nodePush.jjtAddChild(nodeFalse, 0);

        var nodeAdresseGoTo = new ASTJCNbre(JJTJCNBRE);
        nodeAdresseGoTo.jjtSetValue(n + ne1 + ne2 + 3);
        nodeGoto.jjtAddChild(nodeAdresseGoTo, 0);

        return ne1 + ne2 + 3;
    }

    @Override
    public Object visit(ASTou node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];

        var nodeIf = new ASTIf(JJTIF);
        var nodePush = new ASTPush(JJTPUSH);
        var nodeGoto = new ASTGoto(JJTGOTO);

        var ne1 = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
        listInstr.add(nodeIf);
        var ne2 = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ne1 + 1, CompilerMode.DEFAULT));
        listInstr.add(nodeGoto);
        listInstr.add(nodePush);

        var nodeAdresseIf = new ASTJCNbre(JJTJCNBRE);
        nodeAdresseIf.jjtSetValue(n + ne1 + ne2 + 2);
        nodeIf.jjtAddChild(nodeAdresseIf, 0);

        var nodeTrue = new ASTJCVrai(JJTJCVRAI);
        nodeTrue.jjtSetValue(true);
        nodePush.jjtAddChild(nodeTrue, 0);

        var nodeAdresseGoTo = new ASTJCNbre(JJTJCNBRE);
        nodeAdresseGoTo.jjtSetValue(n + ne1 + ne2 + 3);
        nodeGoto.jjtAddChild(nodeAdresseGoTo, 0);

        return ne1 + ne2 + 3;
    }

    @Override
    public Object visit(ASTegal node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTinf node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTsup node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTplus node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTmoins node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTmult node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTdiv node, Object data) throws VisitorException {
        return compileArithmeticOperator(node, data);
    }

    @Override
    public Object visit(ASTlongueur node, Object data) {
        var nodeLength = new ASTLength(JJTLENGTH);
        var nodeIdent = (ASTident) node.jjtGetChild(0);

        var ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue(nodeIdent.jjtGetValue());
        nodeLength.jjtAddChild(ident, 0);
        listInstr.add(nodeLength);

        return 1;
    }

    @Override
    public Object visit(ASTvrai node, Object data) {
        var nodePush = new ASTPush(JJTPUSH);
        var nodeJcVrai = new ASTJCVrai(JJTJCVRAI);

        nodeJcVrai.jjtSetValue(true);
        nodePush.jjtAddChild(nodeJcVrai, 0);

        listInstr.add(nodePush);

        return 1;
    }

    @Override
    public Object visit(ASTfaux node, Object data) {
        var nodePush = new ASTPush(JJTPUSH);
        var nodeJcFalse = new ASTJCFaux(JJTJCFAUX);

        nodeJcFalse.jjtSetValue(false);
        nodePush.jjtAddChild(nodeJcFalse, 0);
        listInstr.add(nodePush);

        return 1;
    }


    @Override
    public Object visit(ASTnbre node, Object data) {
        var nodePush = new ASTPush(JJTPUSH);

        var nbre = new ASTJCNbre(JJTJCNBRE);
        nbre.jjtSetValue(node.jjtGetValue());
        nodePush.jjtAddChild(nbre,0);

        listInstr.add(nodePush);

        return 1;
    }

    @Override
    public Object visit(ASTrien node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTentier node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTbooleen node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTchaine node, Object data) {
        var nodePush = new ASTPush(12);

        //creating node for push(nbre)
        var str = new ASTJCString(41);
        str.jjtSetValue(node.jjtGetValue());
        nodePush.jjtAddChild(str,0);

        listInstr.add(nodePush);

        return 1;
    }

    /* ************************ HELPERS ************************ */

    /**
     * Creates a node ASTNew with all his children
     * @param node Parent node
     * @return The node new
     */
    ASTNew createNodeNew(Node node, Integer s, boolean cst) {
        var nodeNew = new ASTNew(JJTNEW);

        //creating node of new(ident, type, sorte, position)
        var ident = new ASTJCIdent(JJTJCIDENT);
        ident.jjtSetValue(((ASTident) node.jjtGetChild(1)).jjtGetValue());

        var type = new ASTJCType(JJTJCTYPE);
        if (node.jjtGetChild(0).getClass() == ASTentier.class) {
            type.jjtSetValue("entier");
        } else {
            type.jjtSetValue("booleen");
        }

        var sorte = new ASTJCSorte(JJTJCSORTE);
        if (cst)
            sorte.jjtSetValue("cst");
        else
            sorte.jjtSetValue("var");

        var position = new ASTJCNbre(JJTJCNBRE);
        position.jjtSetValue(s);

        //add nodes to new
        nodeNew.jjtAddChild(ident, 0);
        nodeNew.jjtAddChild(type, 1);
        nodeNew.jjtAddChild(sorte, 2);
        nodeNew.jjtAddChild(position, 3);

        return nodeNew;
    }

    private int removeDeclaration() {
        var nodeSwap = new ASTSwap(JJTSWAP);
        var nodePop = new ASTPop(JJTPOP);
        listInstr.add(nodeSwap);
        listInstr.add(nodePop);
        return 2;
    }

    /**
     * Compute the number of headers
     * @param nodeHeaders The headers node
     * @return The number of headers
     */
    private int getHeadersNumber(Node nodeHeaders) {
        var count = 0;
        while (nodeHeaders instanceof ASTentetes) {
            count++;
            nodeHeaders = nodeHeaders.jjtGetChild(1);
        }
        return count;
    }

    /**
     * Compute the type of a node.
     * @param nodeType The Type node
     * @return The type of the node
     */
    private String getTypeFromNode(Node nodeType) {
        if (nodeType instanceof ASTentier)
            return "entier";

        if (nodeType instanceof ASTbooleen)
            return "booleen";

        if (nodeType instanceof ASTrien)
            return "void";

        return null;
    }


    /**
     * Compile variables declarations. Usefull for ASTvars and ASTdelcs
     * @param node Node vars or delcs
     * @param data Compiler data
     * @return
     */
    private Object compileVarDeclaration(Node node, Object data) throws VisitorException {
        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (mode == CompilerMode.DEFAULT) {
            var ndv = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
            var ndvs = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ndv, CompilerMode.DEFAULT));
            return ndv + ndvs;
        }
        if (mode == CompilerMode.REMOVE) {
            var nrdvs = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n, CompilerMode.REMOVE));
            var nrdv = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n + nrdvs, CompilerMode.REMOVE));
            return nrdvs + nrdv;
        }

        return 0;
    }

    /**
     * Comupte the compilation instructions for arithmetic operator according to the instance of the operator node
     * @param node The operator node
     * @param data Compiler data
     * @return
     */
    private int compileArithmeticOperator(Node node, Object data) throws VisitorException {
        var nodeExpLeft = node.jjtGetChild(0);
        var nodeExpRight = node.jjtGetChild(1);
        var nodeOper = createNodeOper(node);

        var cdata = (CompilerData) data;
        var n = (Integer) cdata.args[0];
        var mode = (CompilerMode) cdata.args[1];

        if (mode == CompilerMode.DEFAULT) {

            var ne1 = (int) nodeExpLeft.jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
            var ne2 = (int) nodeExpRight.jjtAccept(this, new CompilerData(n + ne1, CompilerMode.DEFAULT));

            listInstr.add(nodeOper);

            return ne1 + ne2 + 1;
        }

        return 0;
    }

    /**
     * Gives the right JajaCode node operator according to the type of MiniJaja operator node
     * @param node Minijaja operator node
     * @return JajaCode operator node
     */
    private fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node createNodeOper(Node node) {
        fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc.Node nodeOper = null;

        if (node instanceof ASTplus)
            nodeOper = new ASTAdd(JJTADD);

        else if (node instanceof ASTmoins)
            nodeOper = new ASTSub(JJTSUB);

        else if (node instanceof ASTmult)
            nodeOper = new ASTMul(JJTMUL);

        else if (node instanceof ASTdiv)
            nodeOper = new ASTDiv(JajaCodeTreeConstants.JJTDIV);

        else if (node instanceof ASTsup)
            nodeOper = new ASTSup(JajaCodeTreeConstants.JJTSUP);

        else if (node instanceof ASTinf)
            nodeOper = new ASTInf(JajaCodeTreeConstants.JJTINF);

        else if (node instanceof ASTegal)
            nodeOper = new ASTCmp(JJTCMP);

        return nodeOper;
    }

    /**
     * Comupte the compilation instructions for logical operator according to the instance of the operator node
     * @param node The operator node
     * @param data Compiler data
     * @return
     */
//    private int compileLogicalOperator(Node node, Object data) throws VisitorException {
//        var cdata = (CompilerData) data;
//        var n = (Integer) cdata.args[0];
//
//        var ne1 = (int) node.jjtGetChild(0).jjtAccept(this, new CompilerData(n, CompilerMode.DEFAULT));
//
//        var nodeIf = new ASTIf(JJTIF);
//        var nodePush = new ASTPush(JJTPUSH);
//        var nodeGoto = new ASTGoto(JJTGOTO);
//        listInstr.add(nodeIf);
//
//        var ne2 = (int) node.jjtGetChild(1).jjtAccept(this, new CompilerData(n + ne1 + 1, CompilerMode.DEFAULT));
//
//        listInstr.add(nodeGoto);
//        listInstr.add(nodePush);
//
//        var nodeAdresseIf = new ASTJCNbre(JJTJCNBRE);
//        nodeAdresseIf.jjtSetValue(n + ne1 + ne2 + 2);
//        nodeIf.jjtAddChild(nodeAdresseIf, 0);
//
//        if (node instanceof ASTou) {
//            var nodeTrue = new ASTJCVrai(JJTJCVRAI);
//            nodePush.jjtAddChild(nodeTrue, 0);
//        }
//        if (node instanceof ASTet) {
//            var nodeFalse = new ASTJCFaux(JJTFAUX);
//            nodePush.jjtAddChild(nodeFalse, 0);
//        }
//
//        var nodeAdresseGoTo = new ASTJCNbre(JJTJCNBRE);
//        nodeAdresseGoTo.jjtSetValue(n + ne1 + ne2 + 3);
//        nodeGoto.jjtAddChild(nodeAdresseGoTo, 0);
//
//        return ne1 + ne2 + 3;
//    }
}
