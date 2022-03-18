package com.example.appdev;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class S_bookmarkFragment extends Fragment {

    private ArrayList<Job> bookmarkList = new ArrayList<>();
    String userId;
    DatabaseReference jobRef, userRef;
    RecyclerView recyclerView;
    JobListAdapter jobListAdapter;

    public S_bookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_bookmark, container, false);

        jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        jobListAdapter = new JobListAdapter(getContext(), bookmarkList);
        recyclerView.setAdapter(jobListAdapter);

        collectBookmarkJobs();

        return view;
    }

    private void collectBookmarkJobs() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                bookmarkList.clear();
                jobListAdapter.notifyDataSetChanged();
                for (String jobId : user.bookmarkedJobs) {
                    System.out.println(jobId);
                    addJob(jobId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addJob(String jobId) {
        jobRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);
                bookmarkList.add(job);
                jobListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}