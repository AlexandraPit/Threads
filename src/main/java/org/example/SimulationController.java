package org.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.Logic.SimulationManager;
import org.example.Model.Server;
import org.example.Model.Task;

import java.util.concurrent.BlockingQueue;

public class SimulationController {
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
    private Label currentTime;
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

    private SimulationManager simulationManager;
    public void setCurrentTimeLabel(String time) {
        Platform.runLater(() -> this.currentTime.setText(time));
    }

    @FXML
    private void startSimulation() {
        // Retrieve input parameters from UI fields
        int numberOfQueues = Integer.parseInt(nrQues.getText());
        int numberOfClients = Integer.parseInt(nrClients.getText());
        int simulationTime = Integer.parseInt(simTime.getText());
        int arrivalTimeMinValue = Integer.parseInt(arrivalTimeMin.getText());
        int arrivalTimeMaxValue = Integer.parseInt(arrivalTimeMax.getText());
        int serviceTimeMinValue = Integer.parseInt(serviceTimeMin.getText());
        int serviceTimeMaxValue = Integer.parseInt(serviceTimeMax.getText());
        this.simulationManager=new SimulationManager(simulationTime,serviceTimeMaxValue,serviceTimeMinValue,arrivalTimeMaxValue,arrivalTimeMinValue,numberOfQueues,numberOfClients, this);
        // Set simulation parameters in SimulationManager
        //new Thread(simulationManager).start();
        // Run the simulation
        simulationManager.run();

        // Once simulation is done, you can retrieve results from SimulationManager
        // and display them in the UI
        // For example:
        // List<Queue> queues = simulationManager.getQueues();
        // Display queue contents in the UI
        // Note: You need to implement methods in SimulationManager to retrieve queue information
    }
    public void updateQueues(Server[] servers) {
        for (int i = 0; i < servers.length && i < 5; i++) { // Ensure we only iterate up to the number of defined scroll panes
            updateQueue(getScrollPane(i), servers[i].getTasks());
        }
    }



    private ScrollPane getScrollPane(int index) {
        switch (index) {
            case 0:
                return q1;
            case 1:
                return q2;
            case 2:
                return q3;
            case 3:
                return q4;
            case 4:
                return q5;
            default:
                return null;
        }
    }


    private void updateQueue(ScrollPane scrollPane, BlockingQueue<Task> tasks) {
        VBox vbox = new VBox();
        for (Task task : tasks) {
            vbox.getChildren().add(new Label(task.toString()));
        }
        scrollPane.setContent(vbox);
    }

    private VBox getQueueBox(int queueIndex) {
        switch (queueIndex) {
            case 0:
                return q1.getContent() instanceof VBox ? (VBox) q1.getContent() : null;
            case 1:
                return q2.getContent() instanceof VBox ? (VBox) q2.getContent() : null;
            // Add cases for other queues...
            default:
                return null;
        }
    }
}