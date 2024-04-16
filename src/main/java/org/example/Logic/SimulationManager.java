package org.example.Logic;

import javafx.application.Platform;
import org.example.Logic.Strategy.SelectionPolicy;
import org.example.Logic.Strategy.Strategy;
import org.example.Model.Server;
import org.example.Model.Task;
import org.example.SimulationController;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    private int timeLimit;
    private int maxServiceTime;
    private int minServiceTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;

    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private SimulationController controller;

    private AtomicInteger totalWaitingTime = new AtomicInteger(0);
    private AtomicInteger totalServiceTime = new AtomicInteger(0);
    private AtomicInteger peakHour = new AtomicInteger(0);


    public SimulationManager(int timeLimit, int maxServiceTime, int minServiceTime, int maxArrivalTime, int minArrivalTime, int numberOfServers, int numberOfClients, SimulationController controller) {
        this.timeLimit = timeLimit;
        this.minServiceTime=minServiceTime;
        this.maxServiceTime=maxServiceTime;
        this.minArrivalTime=minArrivalTime;
        this.maxArrivalTime=maxArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        scheduler = new Scheduler(numberOfServers, 10); // Assuming 10 is the maximum number of tasks per server
        generatedTasks = Collections.synchronizedList(new ArrayList<>());
        this.controller=controller;
    }

    public void updateTime(int currentTime)
    {
        Platform.runLater(()->{
            controller.setCurrentTimeLabel(currentTime);
        });

    }


    public void run() {
        int currentTime;
        int maxClientsInQueue = 0;
        int currentHour = 0;

        generateNRandomTasks();
        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
        int totalTasks = generatedTasks.size();
        for (Task task : generatedTasks) {
            totalServiceTime.addAndGet(task.getServiceTime());
            System.out.println("id: " + task.getId() + " " + "arrival:  " + task.getArrivalTime() + " service:  " + task.getServiceTime());
        }

        currentTime = 0;
        while (currentTime < timeLimit) {
            updateTime(currentTime);
            int empty = 0;
            List<Server> servers = scheduler.getServers();
            for (Server server : servers) {
                totalWaitingTime.addAndGet(server.getWaitingPeriod().get());
                server.setCurrentTime(currentTime);
                server.setController(controller);
            }

            int totalClientsInQueues = getTotalClientsInQueues(servers, currentTime);
            if (totalClientsInQueues > maxClientsInQueue) {
                maxClientsInQueue = totalClientsInQueues;
                currentHour = currentTime;

            }

            // Update the peak hour
            peakHour.set(currentHour);

            System.out.println("Time " + currentTime);
            Iterator<Task> iterator = generatedTasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();

                if (task.getArrivalTime() == currentTime) {
                    iterator.remove(); // Safely remove the task
                    scheduler.dispatchTask(task);
                }
            }

            System.out.print("Waiting clients: ");
            Platform.runLater(()->controller.updateWaitingClients(generatedTasks));
            for (Task task : generatedTasks) {

                System.out.print(task.toString() + " ");
            }
            System.out.println();

            for (int i = 0; i < servers.size(); i++) {
                Server server = servers.get(i);
                System.out.print("Queue " + (i + 1) + ": ");

                BlockingQueue<Task> tasksInQueue = server.getTasks();

                for (Task task : tasksInQueue) {
                    System.out.print(task.toString());
                }
                System.out.println();

                if (!tasksInQueue.isEmpty()) {
                    empty = 1;
                }
                Platform.runLater(() -> {
                    controller.updateQueues(servers.toArray(new Server[0]));
                });
            }

            if (generatedTasks.isEmpty() && empty == 0) {
                stopServers(servers);
                System.out.println("Simulation Finished");
                break;
            }
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentTime++; // Increment currentTime for the next iteration
        }

        if (totalTasks > 0) {
            double averageWaitingTime = (double) totalWaitingTime.get() / totalTasks;
            double averageServiceTime = (double) totalServiceTime.get() / totalTasks;
           Platform.runLater(()->{ controller.setResults(averageWaitingTime, averageServiceTime,peakHour);});
            // Display or store the average waiting time and average service time
            System.out.println("Average Waiting Time: " + averageWaitingTime);
            System.out.println("Average Service Time: " + averageServiceTime);
        } else {
            // Handle the case where no tasks were processed
            System.out.println("No tasks were processed.");
        }

        // Display or store the peak hour
        System.out.println("Peak Hour: " + peakHour.get());
    }




    private int getTotalClientsInQueues(List<Server> servers, int hour) {
        int totalClients = 0;
        for (Server server : servers) {
            BlockingQueue<Task> tasksInQueue = server.getTasks();
            for (Task task : tasksInQueue) {
                if (task.getArrivalTime() <= hour && hour < task.getArrivalTime() + task.getServiceTime()) {
                    totalClients++;
                }
            }
        }
        return totalClients;
    }

    private void stopServers(List<Server> servers) {
        for (Server server : servers) {
            server.end(); // Signal the server to stop processing tasks
        }
    }
    private void generateNRandomTasks() {
        synchronized (generatedTasks){
            Random random = new Random();
            for (int i = 0; i < numberOfClients; i++) {
                int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
                int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
                Task task = new Task(arrivalTime, serviceTime,i);
                generatedTasks.add(task);
            }
        }
    }

}
