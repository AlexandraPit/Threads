package org.example.Model;

import javafx.application.Platform;
import org.example.Logic.Scheduler;
import org.example.Logic.SimulationManager;
import org.example.Model.Task;
import org.example.SimulationController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private AtomicInteger totalWaitingTime;
    private int id;
    private Scheduler scheduler;
    private SimulationController controller;
    private int currentTime;
    AtomicBoolean done;
    public Server(BlockingQueue<Task> tasks, AtomicInteger waitingPeriod, int id, Scheduler scheduler) {
        this.tasks = tasks;
        this.waitingPeriod = waitingPeriod;
        this.id = id;
        this.scheduler = scheduler;
        this.done=new AtomicBoolean(false);
        this.totalWaitingTime=new AtomicInteger(0);
        System.out.println("Initial waiting period" +waitingPeriod);
    }
    public int getTasksSize() {
        return tasks.size();
    }
    public void setController(SimulationController controller){this.controller=controller;};

    public void addTask(Task newTask) {
        totalWaitingTime.addAndGet(waitingPeriod.get());
            tasks.offer(newTask); // Add task to the queue
        for(int i=0; i<newTask.getServiceTime(); i++) {
            waitingPeriod.incrementAndGet(); // Increment the waiting period
        }
    }
        public void removeTask(Task task) {
        boolean removed = tasks.remove(task);
        if (removed) {
            waitingPeriod.decrementAndGet(); // Decrement the waiting period if the task was removed successfully
        } else {
            System.err.println("Task not found in the server queue");
        }
    }
    public List<Integer> getIds() {
        List<Integer> ids = new ArrayList<>();
        for (Task task : tasks) {
            ids.add(task.getId());
        }
        return ids;
    }
    public void end() {
        done.set(true); // Set done flag to true to stop the server thread
    }

    @Override
    public void run() {
        while (!done.get()) { // Check the done flag
            try {
                Task task = tasks.peek();
                if (task != null) {
                    int serviceT=task.getServiceTime();
                    task.decrementServiceTime();
                    waitingPeriod.decrementAndGet();
                    if (task.getServiceTime() == 0) {
                        synchronized (this) {
                            tasks.poll();
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public AtomicInteger getTotalWaitingTime() {
        return totalWaitingTime;
    }

    private void updateInterface() {
        Platform.runLater(() -> {
            // Update interface components here
            // For example:
             controller.setCurrentTimeLabel(currentTime);
            //controller.updateWaitingPeriod(id, waitingPeriod.get());
        });
    }
    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}
