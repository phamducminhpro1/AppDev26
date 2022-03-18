package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class EditPostActivity extends AppCompatActivity {

    private EditText editTitle, editCompany, editStreet, editCity, editDescription;
    private ImageView imageJob;

    private DatabaseReference reference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageString;
    private StorageTask uploadTask;
    String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Jobs");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

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

        jobId = getIntent().getStringExtra("jobId");
        loadFields();
    }

    private void loadFields() {
        reference.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                editTitle.setText(job.title);
                editCompany.setText(job.company);
                editCity.setText(job.city);
                editStreet.setText(job.street);
                editDescription.setText(job.description);

                // Code for ic_menu_gallery.
                imageJob.setImageResource(17301567);
                if (job.imageUrl != null) {
                    if (!job.imageUrl.isEmpty()) {
                        imageString = job.imageUrl;
                        Glide.with(EditPostActivity.this).load(job.imageUrl).into(imageJob);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onSave(View view) {
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

        Job job = new Job(jobId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                title, company, description, imageString, street, city);
        reference.child(jobId).setValue(job);
        finish();
    }

    public void onDiscard(View view) {
        loadFields();
    }

    public void onDelete(View view) {
        reference.child(jobId).removeValue();
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
                        Glide.with(EditPostActivity.this).load(imageString).into(imageJob);
                        pd.dismiss();
                    } else {
                        Toast.makeText(EditPostActivity.this, "Failed to upload image", Toast.LENGTH_LONG);
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