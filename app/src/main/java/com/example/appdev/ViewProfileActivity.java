package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// This activity gives the user a restricted view at the profile of the person they are chatting with.
// Their first name, last name and profile picture will be shown on top of that:
//  - If the other person is a student it will show their academics.
//  - If the other person is a recruiter it will show information about their company.
public class ViewProfileActivity extends AppCompatActivity {

    // The fields which will be shown to the one viewing the profile.
    private TextView textFirstName, textLastName, textAccountType;
    private ImageView imageProfile;
    private TextView textTitle, textField1, textField2, textField3, textField4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Gather all the fields from the view.
        textFirstName = findViewById(R.id.textFirstName);
        textLastName = findViewById(R.id.textLastName);
        textAccountType = findViewById(R.id.textAccountType);
        textTitle = findViewById(R.id.textViewCategory);
        textField1 = findViewById(R.id.textField1);
        textField2 = findViewById(R.id.textField2);
        textField3 = findViewById(R.id.textField3);
        textField4 = findViewById(R.id.textField4);
        imageProfile = findViewById(R.id.imageProfile);

        // Allow the user to close the activity by hitting the back button in the toolbar.
        Toolbar toolbarViewProfile = findViewById(R.id.toolbarViewProfile);
        toolbarViewProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize all the fields of the profile.
        initializeFields();
    }

    public void initializeFields() {
        // Get the id of the profile we are viewing.
        String userID = getIntent().getStringExtra("userid");

        // Find the user in the database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                // If the user already has existing info, we load that in.
                if (userProfile != null) {
                    initUserProfile(userProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // There was an error in connecting to the database.
            }
        });
    }

    // Loads some of the information of the user into fields which will be shown.
    public void initUserProfile(User userProfile) {
        textField3.setText("");
        textField4.setText("");

        if (userProfile.firstName != null) {
            textFirstName.setText(userProfile.firstName);
        }

        if (userProfile.lastName != null) {
            textLastName.setText(userProfile.lastName);
        }

        if (userProfile.accountType != null) {
            textAccountType.setText(userProfile.accountType.toString());

            // Depending on the account type we load different information.
            if (userProfile.accountType == User.AccountType.RECRUITER) {
                textTitle.setText("Company");
                textField1.setText(userProfile.company);
                textField2.setText(userProfile.sector);
            } else if (userProfile.accountType == User.AccountType.STUDENT) {
                textTitle.setText("Academics");
                textField1.setText(userProfile.studyProgram);
                textField2.setText(userProfile.studyYear);
            }
        }

        // If the user has a profile picture we want to show it.
        imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
        if (userProfile.imageUrl != null) {
            if (!userProfile.imageUrl.equals("")) {
                Glide.with(ViewProfileActivity.this).load(userProfile.imageUrl).into(imageProfile);
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Clicking on the profile picture will send you to a full screen view of the image.
                        Intent intent = new Intent(ViewProfileActivity.this, FullscreenImageActivity.class);
                        intent.putExtra("imageUrl", userProfile.imageUrl);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}