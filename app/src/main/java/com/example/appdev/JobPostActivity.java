package com.example.appdev;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/*
In this activity a recruiter can create a new job posting.
They will be able to provide information such as a title and a description
They can also select an image from their device which will be shown on the job listing.
 */
public class JobPostActivity extends AppCompatActivity {

    private EditText editTitle, editCompany, editStreet, editCity, editDescription;
    private ImageView imageJob;

    private DatabaseReference reference;
    private FileUploader fileUploader;
    private String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        reference = FirebaseDatabase.getInstance().getReference("Jobs");

        // Set up the file uploader.
        fileUploader = new FileUploader(this, getActivityResultRegistry());
        getLifecycle().addObserver(fileUploader);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editTitle = findViewById(R.id.editTextTitle);
        editCompany = findViewById(R.id.editTextCompany);
        editStreet = findViewById(R.id.editTextStreet);
        editCity = findViewById(R.id.editTextCity);
        editDescription = findViewById(R.id.editTextDescription);

        // On clicking the image space they will be allowed to upload a file of type image,
        // within the image type it can be any filetype (jpeg, png etc..)
        imageJob = findViewById(R.id.imageJob);
        imageJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileUploader.openFile("image/*", onImageUploaded);
            }
        });
    }

    // This code will run after the image is uploaded.
    OnCompleteListener<Uri> onImageUploaded = new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            // Check if we successfully uploaded the picture.
            if (task.isSuccessful()) {
                // If the upload was successful, show the image in the image view.
                Uri downloadUri = task.getResult();
                imageString = downloadUri.toString();
                Glide.with(JobPostActivity.this).load(imageString).into(imageJob);
            }
        }
    };

    // Will run when the post button is clicked.
    public void onPost(View view) {
        // Get all the information from the fields.
        String title = editTitle.getText().toString().trim();
        String company = editCompany.getText().toString().trim();
        String street = editStreet.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Check if all the required fields are set.
        if (title.isEmpty()) {
            editTitle.setError("Title required!");
            editTitle.requestFocus();
            return;
        }

        if (company.isEmpty()) {
            editCompany.setError("Company required!");
            editCompany.requestFocus();
            return;
        }

        if (street.isEmpty()) {
            editStreet.setError("Street required!");
            editStreet.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            editCity.setError("City required!");
            editCity.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editDescription.setError("Description required!");
            editDescription.requestFocus();
            return;
        }

        // If all the required fields are filled out, create a job object.
        String jobId = reference.push().getKey();
        LocalDateTime now = java.time.LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        String date = now.format(formatter);

        Job msg = new Job(jobId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                title, company, description, imageString, street, city, date);

        // Store the new job in the database.
        reference.child(jobId).setValue(msg);

        // Close the post job page.
        finish();
    }
}