package fr.ufrst.m1info.projetcomp.m1comp2.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class IDEApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        IDEController controller = new IDEController(new IDEModel(), stage);
        FXMLLoader fxmlLoader = new FXMLLoader(IDEApplication.class.getResource("ide-view.fxml"));
        fxmlLoader.setControllerFactory(t -> controller);
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
        scene.getStylesheets().add(IDEApplication.class.getResource("styles/light-mode.css").toExternalForm());
        stage.setTitle("MiniJaja IDE");
        stage.getIcons().add(new Image(IDEApplication.class.getResource("icons/logo.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
        controller.initCodeArea();
        controller.initJajaCodeResultArea();
        controller.initMiniJajaOutputArea();
        controller.initJajaCodeOutputArea();
        controller.setIconsButton();
        controller.updateIconsMenuItem();
        controller.showHideButtonDebug();
        controller.toggleFileOptionIfOpen();

        KeyCodeController.setUpKeyCodes(controller, scene);
    }

    public static void main(String[] args) {
        launch();
    }
}