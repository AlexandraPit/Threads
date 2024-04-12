package org.example.Logic;

import org.example.Logic.Strategy.ConcreteStrategyTime;
import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler {
    private final List<Server> servers;
    private final int maxNoServers;
    private final int maxTasksPerServer;
    ConcreteStrategyTime concreteStrategyTime;
    private final Lock lock = new ReentrantLock();

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.concreteStrategyTime=new ConcreteStrategyTime();
        servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            BlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
            AtomicInteger waitingPeriod = new AtomicInteger(0);
            Server server = new Server(tasks, waitingPeriod, i, this); // Pass Scheduler instance
            Thread thread = new Thread(server);
            thread.start();
            servers.add(server);
        }
    }

    public void dispatchTask(Task task) {
        lock.lock();
        try {
            concreteStrategyTime.addTask(servers,task);
        } finally {
            lock.unlock();
        }
    }

    private Server getAvailableServer() {
        for (Server server : servers) {
            if (server.getTasksSize()<maxTasksPerServer) {
                return server;
            }
        }
        return null;
    }
    public void updateServiceTime(int currentTime) {
        lock.lock();
        try {
            for (Server server : servers) {
                BlockingQueue<Task> serverTasks = server.getTasks();
                for (Task task : serverTasks) {
                    if(task.getArrivalTime()<=currentTime) {
                        task.decrementServiceTime();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    public List<Server> getServers() {
        lock.lock();
        try {
            return new ArrayList<>(servers);
        } finally {
            lock.unlock();
        }
    }
}
