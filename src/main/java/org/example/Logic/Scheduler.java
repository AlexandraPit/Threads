package org.example.Logic;

import org.example.Logic.Strategy.ConcreteStrategyQueue;
import org.example.Logic.Strategy.ConcreteStrategyTime;
import org.example.Logic.Strategy.SelectionPolicy;
import org.example.Logic.Strategy.Strategy;
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
    ConcreteStrategyTime concreteStrategyTime;
    ConcreteStrategyQueue concreteStrategyQueue;
    private final Lock lock = new ReentrantLock();
    private Strategy strategy;

    public Scheduler(int maxNoServers ) {
        this.maxNoServers = maxNoServers;
        servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            BlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
            AtomicInteger waitingPeriod = new AtomicInteger(0);
            Server server = new Server(tasks, waitingPeriod, i, this); // Pass Scheduler instance
            Thread thread = new Thread(server);
            thread.start();
            servers.add(server);
        }
        strategy=new ConcreteStrategyTime();
    }

    public void changeStrategy(SelectionPolicy policy)
    {lock.lock();
        try {
            if (policy == SelectionPolicy.SHORTEST_QUEUE) {
                strategy = new ConcreteStrategyQueue();
            } else if (policy == SelectionPolicy.SHORTEST_TIME) {
                strategy = new ConcreteStrategyTime();
            } else {
                // Handle unsupported policy
                System.err.println("Unsupported policy: " + policy);
            }
        } finally {
            lock.unlock();
        }
    }
    public void dispatchTask(Task task) {
        lock.lock();
        try {
            if (strategy != null) {
                strategy.addTask(servers, task);
            } else {
                System.err.println("Strategy is not initialized.");
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
