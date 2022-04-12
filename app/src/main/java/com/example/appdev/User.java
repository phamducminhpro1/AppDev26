package com.example.appdev;

import java.util.ArrayList;
import java.util.List;

/*
 This class is used to represent the user in the database.
 */
public class User implements Comparable<User> {

    // Enum for different types of users.
    public enum AccountType {
        NONE, STUDENT, RECRUITER, ADMIN
    }

    // General information about the user.
    public String id, emailAddress, firstName, lastName, phoneNumber, postalAddress,
            studyProgram, studyYear, postalCode, city, company, sector, token;
    // Profile picture.
    public String imageUrl;
    // The type for this specific user.
    public AccountType accountType;
    // A list of all the jobs they have bookmarked.
    public List<String> bookmarkedJobs;

    // Empty constructor for FireBase.
    public User() {
        this.accountType = AccountType.NONE;
        bookmarkedJobs = new ArrayList<>();
    }

    public User(String id, String emailAddress, String firstName, String lastName, AccountType accountType, String token) {
        this.id = id;
        this.accountType = accountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.token = token;
        bookmarkedJobs = new ArrayList<>();
    }

    // Sorts the user based on the alphabetical order.
    // Will first check their first name, then their last name.
    @Override
    public int compareTo(User u) {
        if (firstName.equalsIgnoreCase(u.firstName)) {
            return lastName.toLowerCase().compareTo(u.lastName.toLowerCase());
        }

        return firstName.toLowerCase().compareTo(u.firstName.toLowerCase());
    }
}
