package com.example.appdev;

import java.util.ArrayList;
import java.util.List;

public class Job {
    public String id, title, company, description, imageUrl, street, city, posterId;
    public List<String> appliedStudents;

    public Job() {
        // Leave this here for the database.
        appliedStudents = new ArrayList<>();
    }

    public Job(String id, String posterId, String title, String company, String description,
               String imageUrl, String street, String city) {
        this.id = id;
        this.posterId = posterId;
        this.title = title;
        this.company = company;
        this.description = description;
        this.imageUrl = imageUrl;
        this.street = street;
        this.city = city;
        this.appliedStudents = new ArrayList<>();
    }

    public String getTitle() {
         return title;
    }

    public String getDescription() {
        return description;
    }
}
