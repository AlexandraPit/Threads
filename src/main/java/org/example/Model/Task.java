package org.example.Model;

public class Task {
    private int arrivalTime;
    private int serviceTime;
    private int id;

    public Task(int arrivalTime, int serviceTime, int id) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void decrementServiceTime() {
        if (serviceTime > 0) {
            serviceTime--;
        }
    }
    @Override
    public String toString()
    {
        return "("+id+" "+arrivalTime+" "+serviceTime+")";
    }
}
