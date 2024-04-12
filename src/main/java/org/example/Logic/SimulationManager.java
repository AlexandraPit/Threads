package org.example.Logic;

import javafx.application.Platform;
import org.example.Logic.Strategy.SelectionPolicy;
import org.example.Logic.Strategy.Strategy;
import org.example.Model.Server;
import org.example.Model.Task;
import org.example.SimulationController;

import java.util.*;
import java.util.concurrent.BlockingQueue;

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

    public void run() {
        generateNRandomTasks();
        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrivalTime));
        for (Task task : generatedTasks) {
            System.out.println("id: " + task.getId() + " " + "arrival:  " + task.getArrivalTime() + " service:  " + task.getServiceTime());
        }
        for (int currentTime = 0; currentTime < timeLimit; currentTime++) {
            final int finalCurrentTime = currentTime;
           controller.setCurrentTimeLabel(Integer.toString(finalCurrentTime));
            int empty=0;
            List<Server> servers = scheduler.getServers();
            for (Server server : servers) {
                server.setCurrentTime(currentTime);
            }
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

                if(!tasksInQueue.isEmpty()){empty=1;}

        }
           // controller.updateQueues(servers.toArray(new Server[0]));
            if(generatedTasks.isEmpty()&&empty==0){
                System.out.println("Simulation Finished");
            break;}
            try {
                Thread.sleep(1000); // 1 second delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}

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

/*    public static void main(String[] args) {
        int timeLimit = 15;
        int maxServiceTime = 4;
        int maxArrivalTime = 7;
        int minServiceTime = 2;
        int minArrivalTime = 2;
        int numberOfServers = 2;
        int numberOfClients = 4;

        SimulationManager simulationManager = new SimulationManager(timeLimit, maxServiceTime, minServiceTime,maxArrivalTime,minArrivalTime, numberOfServers, numberOfClients);
        //simulationManager.scheduler.changeStrategy(SelectionPolicy.SHORTEST_QUEUE); // Set the strategy here
        Thread t = new Thread(simulationManager);
        t.start();
        try {
            // Wait for the simulation thread to complete
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

}
