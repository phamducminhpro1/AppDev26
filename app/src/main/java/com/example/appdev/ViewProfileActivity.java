package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView textFirstName, textLastName;
    private ImageView imageProfile;
    private TextView textAccountType;
    private TextView textTitle, textField1, textField2, textField3, textField4;
    private Toolbar toolbarViewProfile;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        textFirstName = findViewById(R.id.textFirstName);
        textLastName = findViewById(R.id.textLastName);
        textAccountType = findViewById(R.id.textAccountType);
        textTitle = findViewById(R.id.textViewCategory);
        textField1 = findViewById(R.id.textField1);
        textField2 = findViewById(R.id.textField2);
        textField3 = findViewById(R.id.textField3);
        textField4 = findViewById(R.id.textField4);
        imageProfile = findViewById(R.id.imageProfile);

        toolbarViewProfile = findViewById(R.id.toolbarViewProfile);
        toolbarViewProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeFields();
    }

    public void initializeFields() {
        String userID = getIntent().getStringExtra("userid");
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

            }
        });
    }

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

        imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
        if (userProfile.imageUrl != null) {
            if (!userProfile.imageUrl.equals("")) {
                Glide.with(ViewProfileActivity.this).load(userProfile.imageUrl).into(imageProfile);
                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewProfileActivity.this, FullscreenImageActivity.class);
                        intent.putExtra("imageUrl", userProfile.imageUrl);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}