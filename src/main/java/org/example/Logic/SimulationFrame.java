package org.example.Logic;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

public class SimulationFrame {

    @FXML
    private TextField nrQues;

    @FXML
    private TextField nrClients;

    @FXML
    private TextField simTime;

    @FXML
    private TextField arrivalTimeMin;

    @FXML
    private TextField arrivalTimeMax;

    @FXML
    private TextField serviceTimeMin;

    @FXML
    private TextField serviceTimeMax;

    @FXML
    private Button startSimulation;

    @FXML
    private ScrollPane q1;

    @FXML
    private ScrollPane q2;

    @FXML
    private ScrollPane q3;

    @FXML
    private ScrollPane q4;

    @FXML
    private ScrollPane q5;

    @FXML
    private void initialize() {
        // Initialize method called after loading the FXML file
        // You can perform any initialization here
    }

    @FXML
    private void startSimulation() {
        // Event handler for the "Start Simulation" button
        // This method will be called when the button is clicked
        // You can implement the simulation logic here
    }

    // You may need additional methods here for handling other UI interactions

}
