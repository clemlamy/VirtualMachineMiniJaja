/* Generated By:JJTree: Do not edit this line. ASTGoto.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

public
class ASTGoto extends SimpleNode {
  public ASTGoto(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(JajaCodeVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f759f8debecda9ed291d84da73007dd7 (do not edit this line) */
