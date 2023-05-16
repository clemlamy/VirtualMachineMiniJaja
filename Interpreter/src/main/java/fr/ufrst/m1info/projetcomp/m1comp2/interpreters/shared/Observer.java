package fr.ufrst.m1info.projetcomp.m1comp2.interpreters.shared;

import java.util.List;

public interface Observer {
    void notifyChange(List<String> stack);
    void printInJajaCodeOutput(String s);
    void printInMiniJajaCodeOutput(String s);
    void hideButtons();
    void highlightJjc(int i);
    void highlightMjj(int i);
}
