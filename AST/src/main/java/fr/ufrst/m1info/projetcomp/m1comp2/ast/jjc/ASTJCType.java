/* Generated By:JJTree: Do not edit this line. ASTJCType.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package fr.ufrst.m1info.projetcomp.m1comp2.ast.jjc;


import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import fr.ufrst.m1info.projetcomp.m1comp2.ast.mjj.VisitorException;

public
class ASTJCType extends SimpleNode {
  public ASTJCType(int id) {
    super(id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JajaCodeVisitor visitor, Object data) throws VisitorException {

    return
    visitor.visit(this, data);
  }

  public Type getType() {
    String type = (String) jjtGetValue();
    switch (type) {
      case "entier": return Type.INTEGER;
      case "booleen": return Type.BOOLEAN;
      case "void": return Type.VOID;
    }
    return null;
  }
}
/* JavaCC - OriginalChecksum=725ba360c8721cea764120ac5e7ba3bb (do not edit this line) */