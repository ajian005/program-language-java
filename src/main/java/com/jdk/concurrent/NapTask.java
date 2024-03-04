package com.jdk.concurrent;

public class NapTask implements Runnable{
    final int id;

    public NapTask(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        new Nap(0.1); // Seconds
        System.out.println(this + " " + Thread.currentThread().getName());
    }

    public String toString() {
        return "NapTask[" + id + "]";
    }
}
