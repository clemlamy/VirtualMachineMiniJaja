module fr.ufrst.m1info.projetcomp.m1comp2.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires Analyser;
    requires AST;
    requires Compiler;
    requires Interpreter;
    requires Memory;
    requires fr.ufrst.m1info.projetcomp.m1comp2;


    opens fr.ufrst.m1info.projetcomp.m1comp2.gui to javafx.fxml;
    exports fr.ufrst.m1info.projetcomp.m1comp2.gui;
}