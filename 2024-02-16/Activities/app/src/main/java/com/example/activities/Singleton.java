package com.example.activities;

public class Singleton {
    static final private Singleton instance = new Singleton();
    public String message = "";
    private Singleton(){

    }
    public static Singleton getInstance(){
        return instance;
    }
}
