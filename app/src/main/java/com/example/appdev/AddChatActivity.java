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
This activity will be entered when hitting the plus sign from the chat tab.
It should show a list of all the users in the database.
From there the user can click on one of the users to start a chat with them.
 */
public class AddChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        // The activity will close when you hit the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        readUsers();
    }

    private void readUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list of users before updating it.
                mUsers.clear();

                // Loop through all users in the database.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    // We don't allow chatting with yourself.
                    if (!user.id.equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                // Sort the users alphabetically.
                Collections.sort(mUsers);

                // Update the adapter and recyclerview to use the new list of users.
                chatListAdapter = new ChatListAdapter(AddChatActivity.this, mUsers);
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // We failed to read the data from the database.
            }
        });
    }
}