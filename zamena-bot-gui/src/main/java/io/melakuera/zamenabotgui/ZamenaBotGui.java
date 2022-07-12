package io.melakuera.zamenabotgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ZamenaBotGui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainScene.fxml"));


        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Графический интерфейс Zamena Bot GUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        ZamenaBotController controller = fxmlLoader.getController();
        controller.setStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}