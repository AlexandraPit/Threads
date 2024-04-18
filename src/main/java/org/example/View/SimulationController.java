package org.example.View;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.Logic.Scheduler;
import org.example.Logic.SimulationManager;
import org.example.Logic.Strategy.SelectionPolicy;
import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML
    private VBox waitingClients;
    @FXML
    private VBox results;
    @FXML
    private ChoiceBox<SelectionPolicy> strategyChoiceBox;
    @FXML
    private Label error;
    private SimulationManager simulationManager;
    private Scheduler scheduler;
    public void setCurrentTimeLabel(int time) {
       this.currentTime.setText(Integer.toString(time));
    }

    public SimulationController() {
        // Initialize the scheduler here
        this.scheduler = new Scheduler(1); // You may need to adjust the constructor parameters
    }
    @FXML
    private void startSimulation() {
        error.setText("");
        // Retrieve input parameters from UI fields
        int numberOfQueues = Integer.parseInt(nrQues.getText());
        int numberOfClients = Integer.parseInt(nrClients.getText());
        int simulationTime = Integer.parseInt(simTime.getText());
        int arrivalTimeMinValue = Integer.parseInt(arrivalTimeMin.getText());
        int arrivalTimeMaxValue = Integer.parseInt(arrivalTimeMax.getText());
        int serviceTimeMinValue = Integer.parseInt(serviceTimeMin.getText());
        int serviceTimeMaxValue = Integer.parseInt(serviceTimeMax.getText());

        if (numberOfQueues > 5) {
            // Display an error message to the user
            // You can choose how to handle the error, such as showing a dialog box or updating a label
            error.setText("Error: Number of queues is too large.\nMaximum number of queues allowed is 5.");
            System.err.println("Error: Number of queues is too large. Maximum number of queues allowed is 5.");
            return; // Exit the method without starting the simulation
        }

        this.simulationManager=new SimulationManager(simulationTime,serviceTimeMaxValue,serviceTimeMinValue,arrivalTimeMaxValue,arrivalTimeMinValue,numberOfQueues,numberOfClients, this);
        Thread simulationThread=new Thread(simulationManager);
        this.scheduler=simulationManager.getScheduler();
        simulationThread.start();

    }
    public void updateQueues(Server[] servers) {
        for (int i = 0; i < servers.length && i < 5; i++) { // Ensure we only iterate up to the number of defined scroll panes
            updateQueue(getScrollPane(i), servers[i].getTasks());
        }
    }



    // Method to initialize the choice box
    @FXML
    public void initialize() {

        // Populate the choice box with strategy options
        strategyChoiceBox.getItems().addAll(SelectionPolicy.SHORTEST_QUEUE, SelectionPolicy.SHORTEST_TIME);

        // Set a default selection
        strategyChoiceBox.setValue(SelectionPolicy.SHORTEST_QUEUE);

        // Add an event listener to the choice box
        strategyChoiceBox.setOnAction(event -> {
            SelectionPolicy selectedStrategy = strategyChoiceBox.getValue();
            scheduler.changeStrategy(selectedStrategy);
        });
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
        vbox.setAlignment(Pos.TOP_CENTER);
        for (Task task : tasks) {

            Label clientsLabel = new Label(task.toString());
            clientsLabel.setFont(Font.font("Georgia", 14)); // Set font size and type
            vbox.getChildren().add(clientsLabel);
        }
        scrollPane.setContent(vbox);
    }
    public void updateWaitingClients(List<Task> clients){
        waitingClients.setAlignment(Pos.TOP_CENTER);
        waitingClients.getChildren().clear();
        for( Task task : clients)
        {
            Label waitingLabel = new Label( task.toString());
            waitingLabel.setFont(Font.font("Georgia", 14)); // Set font size and type
            //waitingLabel.setTextFill(Color.WHITE);
            waitingClients.getChildren().add(waitingLabel);
        }}
    public void setResults(double avgWaitingTime, double avgServiceTime, AtomicInteger peakhour){
        results.getChildren().clear();
        Label avgWaitingLabel = new Label("  Average Waiting Time: " + avgWaitingTime);
        avgWaitingLabel.setFont(Font.font("Georgia", 14)); // Set font size and type
        avgWaitingLabel.setTextFill(Color.WHITE); // Set text color

        Label avgServiceLabel = new Label("  Average Service Time: " + avgServiceTime);
        avgServiceLabel.setFont(Font.font("Georgia", 14)); // Set font size and type
        avgServiceLabel.setTextFill(Color.WHITE);

        Label resLabel = new Label("  Simulation Results: ");
        resLabel.setFont(Font.font("Georgia", 14)); // Set font size and type


        Label peakLabel = new Label("  Peak Hour: " + peakhour);
        peakLabel.setFont(Font.font("Georgia", 14)); // Set font size and type
        peakLabel.setTextFill(Color.WHITE);
        Label spacer1 = new Label("");
        Label spacer2 = new Label("");
        Label spacer3 = new Label("");
        Label spacer4 = new Label("");
        results.setStyle("-fx-background-color: #AA7BC3;");
        results.getChildren().add(spacer4);
        results.getChildren().add(resLabel);
        results.getChildren().add(spacer1);
        results.getChildren().add(avgWaitingLabel);
        results.getChildren().add(spacer2);
        results.getChildren().add(avgServiceLabel);
        results.getChildren().add(spacer3);
        results.getChildren().add(peakLabel);
    }
}