/* Generated By:JJTree: Do not edit this line. ASTmethode.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj;



public
class ASTmethode extends SimpleNode {
  public ASTmethode(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(MiniJajaVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5293fa4d0e1f4692995d93fa6bf574c5 (do not edit this line) */