/* Generated By:JJTree: Do not edit this line. ASTNot.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

public
class ASTNot extends SimpleNode {
  public ASTNot(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(JajaCodeVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=230cedd46d65eb2f2d566c301ae07b04 (do not edit this line) */