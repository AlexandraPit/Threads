package org.example.Logic.Strategy;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public Server addTask(List<Server> servers, Task task) {
        // Find the server with the shortest queue
        Server shortestQueueServer = servers.get(0);
        for (Server server : servers) {
            if (server.getTasks().size() < shortestQueueServer.getTasks().size()) {
                shortestQueueServer = server;
            }
        }
        // Add the task to the server with the shortest queue
        shortestQueueServer.addTask(task);
        return shortestQueueServer;
    }
}
