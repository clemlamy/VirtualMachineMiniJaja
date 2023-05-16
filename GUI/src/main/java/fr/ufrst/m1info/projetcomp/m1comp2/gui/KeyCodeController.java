package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class KeyCodeController {

    public static void setUpKeyCodes(IDEController controller, Scene scene) {
        // Shortcut CTRL + L to clean output
        KeyCombination keyCtrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_ANY);
        scene.getAccelerators().put(keyCtrlL, controller::cleanMiniJajaOutput);
    }
}
