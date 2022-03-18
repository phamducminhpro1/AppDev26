package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobDescriptionActivity extends AppCompatActivity {
    private Toolbar toolbarJobDescription;
    private ImageView mainImageView;
    private TextView title, description, city, address, company;
    private Button bookmarkButton;

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

        toolbarJobDescription = findViewById(R.id.toolbarJobDescription);
        toolbarJobDescription.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        jobId = getIntent().getStringExtra("jobId");

        readJobInfo();

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookmarkInfo();
            }
        });
    }

    private void readBookmarkInfo() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

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

    private void setBookmarkInfo() {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.bookmarkedJobs.contains(jobId)) {
                    user.bookmarkedJobs.remove(jobId);
                } else {
                    user.bookmarkedJobs.add(jobId);
                }

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

                if (job.imageUrl != null) {
                    if (!job.imageUrl.isEmpty()) {
                        Glide.with(getApplicationContext()).load(job.imageUrl).into(mainImageView);
                    }
                }
                readBookmarkInfo();

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