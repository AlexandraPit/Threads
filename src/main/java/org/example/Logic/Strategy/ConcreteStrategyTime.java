package org.example.Logic.Strategy;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public Server addTask(List<Server> servers, Task task) {
        // Find the server with the shortest service time
        Server shortestServer = servers.get(0);
        for (Server server : servers) {
            if (server.getTasks().size() < shortestServer.getTasks().size()) {
                shortestServer = server;
            }
        }
        // Add the task to the server with the shortest service time
        shortestServer.addTask(task);
        return shortestServer;
    }
}
