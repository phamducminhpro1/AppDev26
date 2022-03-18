package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobDescriptionActivity extends AppCompatActivity {
    Toolbar toolbarJobDescription;
    ImageView mainImageView;
    TextView title, description, city, address, company;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);

        mainImageView = findViewById(R.id.mainImageView);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        city = findViewById(R.id.CityText);
        address = findViewById(R.id.AddressText);
        company = findViewById(R.id.CompanyText);
        toolbarJobDescription = findViewById(R.id.toolbarJobDescription);
        toolbarJobDescription.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Jobs");
        String jobId = getIntent().getStringExtra("jobId");
        System.out.println(jobId);
        readJobInfo(jobId);
    }

    private void readJobInfo(String jobId) {
        reference.child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                if (job.imageUrl != null) {
                    if (!job.imageUrl.isEmpty()) {
                        Glide.with(getApplicationContext()).load(job.imageUrl).into(mainImageView);
                    }
                }

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