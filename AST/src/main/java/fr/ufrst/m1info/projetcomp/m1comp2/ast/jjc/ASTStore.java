/* Generated By:JJTree: Do not edit this line. ASTStore.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

public
class ASTStore extends SimpleNode {
  public ASTStore(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(JajaCodeVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=882128fa86f1b96eba4906c66ff8ce57 (do not edit this line) */
