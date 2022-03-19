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

/*
The edit post activity allows recruiters to edit all the fields of a previously made post.
It also allows them to fully delete the post if they want to.
To get to this activity, a recruiter can hit the edit button in their list of posts.
 */
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

        // The activity will close when you hit the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Store the database references to the jobs and uploads.
        reference = FirebaseDatabase.getInstance().getReference("Jobs");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // Collect all the UI elements.
        editTitle = findViewById(R.id.editTextTitle);
        editCompany = findViewById(R.id.editTextCompany);
        editStreet = findViewById(R.id.editTextStreet);
        editCity = findViewById(R.id.editTextCity);
        editDescription = findViewById(R.id.editTextDescription);

        // When clicking on the image of the job post
        // it will open the file app to select a new image for the job post.
        imageJob = findViewById(R.id.imageJob);
        imageJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        // Get the job id from the intent so we know which job is being edited.
        jobId = getIntent().getStringExtra("jobId");

        // Load all the current information into the fields.
        loadFields();
    }

    private void loadFields() {
        // Read the job from the database.
        reference.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                // Set all the fields to the current values.
                editTitle.setText(job.title);
                editCompany.setText(job.company);
                editCity.setText(job.city);
                editStreet.setText(job.street);
                editDescription.setText(job.description);

                // Code for ic_menu_gallery we use this if there is no image.
                imageJob.setImageResource(17301567);

                // If there is an image we load this.
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
        // Gather all the fields into strings.
        String title = editTitle.getText().toString().trim();
        String company = editCompany.getText().toString().trim();
        String street = editStreet.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        // Check if none of the required fields are empty.
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

        // If none of the required fields were empty we can store the new job in the database.
        Job job = new Job(jobId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                title, company, description, imageString, street, city);
        reference.child(jobId).setValue(job);

        // Close the edit job page.
        finish();
    }

    // To discard any edits we can simply load everything from the database again.
    public void onDiscard(View view) {
        loadFields();
    }

    // Delete the job post and exit the edit activity.
    public void onDelete(View view) {
        reference.child(jobId).removeValue();
        finish();
    }

    // Opens the file app and allows the user to select images.
    public void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    // Gives the files type.
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        // Show a loading screen while the images is still being uploaded.
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            // Store the file into the database with the time as the filename.
            // Doing this means that the chances of duplicate names are very low.
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
                    // We successfully uploaded the picture
                    if (task.isSuccessful()) {
                        // Get the uploaded image.
                        Uri downloadUri = task.getResult();
                        imageString = downloadUri.toString();

                        // Show the image in the UI.
                        Glide.with(EditPostActivity.this).load(imageString).into(imageJob);

                        // Close the loading screen.
                        pd.dismiss();
                    } else {
                        // Show the user something went wrong with uploading the image.
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