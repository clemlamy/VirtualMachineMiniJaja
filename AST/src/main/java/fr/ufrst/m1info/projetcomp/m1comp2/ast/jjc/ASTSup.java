/* Generated By:JJTree: Do not edit this line. ASTSup.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

public
class ASTSup extends SimpleNode {
  public ASTSup(int id) {
    super(id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(JajaCodeVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=81fd24044894e1f67fc680ecedc0661d (do not edit this line) */
