/* Generated By:JJTree: Do not edit this line. ASTtab.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj;



public
class ASTtab extends SimpleNode {
  public ASTtab(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(MiniJajaVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=b6927ed0438a2bdcc7ad5cdba05f8330 (do not edit this line) */
