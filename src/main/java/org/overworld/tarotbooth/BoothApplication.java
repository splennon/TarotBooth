package org.overworld.tarotbooth;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BoothApplication extends Application {

    private static Scene threeCardScene, ezzieScene;

    Stage ezzieStage = new Stage();
    Stage threeCardStage = new Stage();

    @Override
    public void start(Stage prinaryStage) throws IOException {

        threeCardScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("threeCardSpread.fxml")).load(), 640, 480);
        threeCardStage.setScene(threeCardScene);
        threeCardStage.show();
        
        ezzieScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("ezzie.fxml")).load(), 640, 480);
        ezzieStage.setScene(ezzieScene);
        ezzieStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
