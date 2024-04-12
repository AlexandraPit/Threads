package org.example.Logic.Strategy;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public interface Strategy {
    public Server addTask(List<Server> server, Task t);
}
