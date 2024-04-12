package org.example.Model;

import org.example.Logic.Scheduler;
import org.example.Logic.SimulationManager;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private int id;
    private Scheduler scheduler;
    private int currentTime;
    public Server(BlockingQueue<Task> tasks, AtomicInteger waitingPeriod, int id, Scheduler scheduler) {
        this.tasks = tasks;
        this.waitingPeriod = waitingPeriod;
        this.id = id;
        this.scheduler = scheduler;

    }
    public int getTasksSize() {
        return tasks.size();
    }

    public void addTask(Task newTask) {

            tasks.offer(newTask); // Add task to the queue
            waitingPeriod.incrementAndGet(); // Increment the waiting period

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
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = tasks.peek();
                if (task != null) {
                    task.decrementServiceTime();
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

    private void executeTask(Task task) throws InterruptedException {
        /* int remainingServiceTime = task.getServiceTime();
        while (remainingServiceTime > 0) {
             // Simulate task processing time
            remainingServiceTime--;
            task.decrementServiceTime();
        }*/
       // Thread.sleep(1000);\
        int t=0;
        while(task.getArrivalTime()>=currentTime && task.getServiceTime()>0) {
            task.decrementServiceTime();
            t++;
        }
        if(task.getServiceTime()==0) {
            tasks.poll();
           // removeTask(task);
        }
        //waitingPeriod.decrementAndGet(); // Decrement the waiting period
        System.out.println("Task " + task.getId() + " completed by Server " + id + " at time " + (currentTime+t));       // scheduler.updateServiceTime();
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    /*    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }*/
    public void updateServiceTime() {
        for (Task task : tasks) {
            task.decrementServiceTime();
        }
    }
}
