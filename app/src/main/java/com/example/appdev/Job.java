package com.example.appdev;

public class Job {
    public String id, title, company, description, imageUrl, street, city, posterId;

    public Job() {
        // Leave this here for the database.
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
    }

    public String getTitle() {
         return title;
    }

    public String getDescription() {
        return description;
    }
}
