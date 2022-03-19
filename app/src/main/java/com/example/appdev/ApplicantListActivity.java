package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
This is activity will be shown to recruiters if they click on a job they posted.
It shows a list of all the people who applied for that job.
From there the recruiter can click on someone to start a chat with them.
 */
public class ApplicantListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<User> mUsers;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list);

        // The activity will close when you hit the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUsers = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(this, mUsers);
        recyclerView.setAdapter(chatListAdapter);

        // Retrieve the job from the intent.
        jobId = getIntent().getStringExtra("jobId");

        readUsers();
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jobs");

        // Get the job we are interested in from the database.
        reference.child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);

                // Clear the list and notify the adapater.
                mUsers.clear();
                chatListAdapter.notifyDataSetChanged();

                // Loop through everyone who has applied and add them.
                for (String id : job.appliedStudents) {
                    addUser(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // We failed to read the data from the database.
            }
        });
    }

    private void addUser(String id) {
        // Get the user from the database based on the id.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Add the user to the list and notify the adapter that the data has changed.
                mUsers.add(user);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // We failed to read the data from the database.
            }
        });
    }
}