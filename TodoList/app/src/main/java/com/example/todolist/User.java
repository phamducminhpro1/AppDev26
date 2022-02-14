package com.example.todolist;

import java.util.ArrayList;

public class User {
    public String emailAddress;
    public ArrayList<Task> tasks;

    public User() {
        tasks = new ArrayList<>();
        tasks.add(new Task("Test"));
    }

    public User(String emailAddress) {
        this.emailAddress = emailAddress;
        tasks = new ArrayList<>();
        tasks.add(new Task("Test"));
    }
}
