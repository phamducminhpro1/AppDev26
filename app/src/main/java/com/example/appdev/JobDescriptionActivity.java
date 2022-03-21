package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
Activity which shows more details about a job.
Student can find this page after clicking on a job.
From here they can also bookmark or apply for a job.
 */
public class JobDescriptionActivity extends AppCompatActivity {
    private Toolbar toolbarJobDescription;
    private ImageView mainImageView;
    private TextView title, description, city, address, company;
    private Button bookmarkButton, applyButton;

    private String jobId, userId;
    private DatabaseReference jobRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mainImageView = findViewById(R.id.mainImageView);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        city = findViewById(R.id.CityText);
        address = findViewById(R.id.AddressText);
        company = findViewById(R.id.CompanyText);

        bookmarkButton = findViewById(R.id.bookmarkButton);
        applyButton = findViewById(R.id.applyButton);

        toolbarJobDescription = findViewById(R.id.toolbarJobDescription);
        toolbarJobDescription.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // References to the jobs and users in the database.s
        jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        jobId = getIntent().getStringExtra("jobId");

        // Initialize the page by reading all the info of this job from the database.
        readJobInfo();

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookmarkInfo();
            }
        });
    }

    // Toggles between applying and withdrawing from a job.
    private void setApply(Job job) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // If the student has already applied, we remove them.
                if (job.appliedStudents.contains(userId)) {
                    job.appliedStudents.remove(userId);
                } else {
                    // If the student has not applied yet, we add them to the list of applied students.
                    job.appliedStudents.add(userId);
                }

                // Update the job information in the database.
                jobRef.child(jobId).setValue(job);

                // On the user side we also want to update the jobs they applied to.
                // If they already applied, remove this job from the list of jobs they applied to.
                if (user.appliedJobs.contains(jobId)) {
                    user.appliedJobs.remove(jobId);
                } else {
                    // If they haven't applied yet, add this job to the list and
                    // send the student to a chat with the poster of the job listing.
                    user.appliedJobs.add(jobId);
                    Intent intent = new Intent(JobDescriptionActivity.this, MessageActivity.class);
                    intent.putExtra("userid", job.posterId);
                    startActivity(intent);
                }

                // Update the user information in the database.
                userRef.child(userId).setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Read if the student has applied for this job and update the buttons accordingly.
    private void readApply() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Change the text of the bookmark button depending on
                // whether a job is bookmarked or not
                if (user.appliedJobs.contains(jobId)) {
                    applyButton.setText("Withdraw");
                } else {
                    applyButton.setText("Apply");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Read if the student has bookmarked this job and update the button accordingly.
    private void readBookmarkInfo() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Change the icon of the bookmark button depending on
                // whether a job is bookmarked or not
                if (user.bookmarkedJobs.contains(jobId)) {
                    bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                } else {
                    bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Toggle between having the job bookmarked or not.
    private void setBookmarkInfo() {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Flip whether the job is stored as bookmarked or not in the user class.
                if (user.bookmarkedJobs.contains(jobId)) {
                    user.bookmarkedJobs.remove(jobId);
                } else {
                    user.bookmarkedJobs.add(jobId);
                }

                // Update the user information in the database.
                userRef.child(userId).setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readJobInfo() {
        jobRef.child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                // If the job has an image we load it into the image view.
                if (job.imageUrl != null) {
                    if (!job.imageUrl.isEmpty()) {
                        Glide.with(getApplicationContext()).load(job.imageUrl).into(mainImageView);
                    }
                }

                // Initialize the bookmark button correctly.
                readBookmarkInfo();

                // Initialize the apply button correctly.
                readApply();

                // When clicking the apply button toggle the application for this job.
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setApply(job);
                    }
                });

                // Update all the job fields with the information from the database.
                title.setText(job.title);
                description.setText(job.description);
                city.setText(job.city);
                address.setText(job.street);
                company.setText(job.company);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}