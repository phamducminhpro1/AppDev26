package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class JobPostActivity extends AppCompatActivity {

    private EditText editTitle, editCompany, editStreet, editCity, editDescription;
    private ImageView imageJob;

    private DatabaseReference reference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageString;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        reference = FirebaseDatabase.getInstance().getReference("Jobs");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

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

        imageJob = findViewById(R.id.imageJob);
        imageJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
    }

    public void onPost(View view) {
        String title = editTitle.getText().toString().trim();
        String company = editCompany.getText().toString().trim();
        String street = editStreet.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

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

        String jobId = reference.push().getKey();
        Job msg = new Job(jobId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                title, company, description, imageString, street, city);
        reference.child(jobId).setValue(msg);
        finish();
    }

    public void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageString = downloadUri.toString();
                        Glide.with(JobPostActivity.this).load(imageString).into(imageJob);
                        pd.dismiss();
                    } else {
                        Toast.makeText(JobPostActivity.this, "Failed to upload image", Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Uploading image", Toast.LENGTH_LONG);
            } else {
                uploadImage();
            }

        }
    }
}