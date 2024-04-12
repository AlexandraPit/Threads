package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Logic.SimulationManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Parent root = loader.load();

        // Set up the JavaFX scene
        primaryStage.setTitle("Queue Simulation");
        primaryStage.setScene(new Scene(root, 870, 604));
        primaryStage.show();

        // Get the controller
        SimulationController controller = loader.getController();



    }

    public static void main(String[] args) {
        launch(args);
    }
}
