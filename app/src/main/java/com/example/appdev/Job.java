package com.example.appdev;

public class Job {
    public String title, company, description, imageUrl, street, city;

    public Job() {}

    public Job(String title, String company, String description,
               String imageUrl, String street, String city) {
        this.title = title;
        this.company = company;
        this.description = description;
        this.imageUrl = imageUrl;
        this.street = street;
        this.city = city;
    }
}
