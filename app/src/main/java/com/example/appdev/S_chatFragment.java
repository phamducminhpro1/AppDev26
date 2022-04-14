package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class S_chatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;

    // List of all the users which get shown.
    private List<User> mUsers = new ArrayList<>();

    // List of all id's of users we have messaged with before.
    private List<String> ids = new ArrayList<>();

    public S_chatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        readMessages();

        // Upon clicking on the plus sign, open the add chat activity.
        TextView textAddChat = view.findViewById(R.id.textAddChat);
        textAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddChatActivity.class));
            }
        });

        return view;
    }

    private void readMessages() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Empty the list of ids.
                ids.clear();

                // Loop through all the messages.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message msg = snapshot.getValue(Message.class);

                    // If we have send a message to this user, add them to ids.
                    if (msg.sender.equals(firebaseUser.getUid())) {
                        if (!ids.contains(msg.receiver)) {
                            ids.add(msg.receiver);
                        }
                    }

                    // If we have received a message from this user, add them to ids.
                    if (msg.receiver.equals(firebaseUser.getUid())) {
                        if (!ids.contains(msg.sender)) {
                            ids.add(msg.sender);
                        }
                    }
                }

                // After collecting the correct ids, we can collect the actual users.
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                // Loop through all the users.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    // If the id of this user is also in the list of ids, add them to the list.
                    if (ids.contains(user.id)) {
                        mUsers.add(user);
                    }
                }

                // Sort the user on their name.
                Collections.sort(mUsers);

                // Update the adapter and recyclerview to show the new list of users.
                chatListAdapter = new ChatListAdapter(getContext(), mUsers);
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}