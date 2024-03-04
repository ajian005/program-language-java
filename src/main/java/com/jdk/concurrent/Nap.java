package com.jdk.concurrent;

import java.util.concurrent.TimeUnit;

public class Nap {
    public Nap(double t) {  // Seconds
        try {
            TimeUnit.MILLISECONDS.sleep((int)(1000 * t));
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Nap(double t, String msg) {
        this(t);
        System.out.println(msg);
    }
}