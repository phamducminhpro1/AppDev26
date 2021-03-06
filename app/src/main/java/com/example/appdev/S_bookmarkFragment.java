package com.example.appdev;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

public class S_bookmarkFragment extends Fragment {

    // List of jobs the user has bookmarked.
    private ArrayList<Job> bookmarkList = new ArrayList<>();

    // List of jobs the user has applied to.
    private ArrayList<Job> applyList = new ArrayList<>();

    // The id of the user.
    private String userId;

    // References to the job and user parts of the database.
    private DatabaseReference jobRef, userRef;

    // The recyclerviews which represents the lists of bookmarked and applied jobs
    private RecyclerView recyclerViewBookmarked, recyclerViewApplied;
    private JobListAdapter bookmarkListAdapter, applyListAdapter;

    // The expandable layout which is used to expand and collapse the lists.
    private ExpandableLayout expandableBookmarked, expandableApplied;

    public S_bookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_bookmark, container, false);

        // Get the references
        jobRef = FirebaseDatabase.getInstance().getReference("Jobs");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        expandableBookmarked = view.findViewById(R.id.expandableBookmarked);
        expandableApplied = view.findViewById(R.id.expandableApplied);

        // When clicking on the title of the applied list, it should expand or collapse.
        TextView textApplied = view.findViewById(R.id.expandTextApplied);
        textApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickApplied();
            }
        });

        // When clicking on the title of the bookmarked list, it should expand or collapse.
        TextView textBookmarked = view.findViewById(R.id.expandTextBookmarked);
        textBookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBookmarked();
            }
        });

        // Initialize the bookmarked list.
        recyclerViewBookmarked = view.findViewById(R.id.recyclerViewBookmarked);
        recyclerViewBookmarked.setLayoutManager(new LinearLayoutManager(view.getContext()));
        bookmarkListAdapter = new JobListAdapter(getContext(), bookmarkList);
        recyclerViewBookmarked.setAdapter(bookmarkListAdapter);
        collectBookmarkJobs();

        // Initialize the list of jobs we applied to.
        recyclerViewApplied = view.findViewById(R.id.recyclerViewApplied);
        recyclerViewApplied.setLayoutManager(new LinearLayoutManager(view.getContext()));
        applyListAdapter = new JobListAdapter(getContext(), applyList);
        recyclerViewApplied.setAdapter(applyListAdapter);
        collectAppliedJobs();

        return view;
    }

    // If the applied list is expanded, collapse and vice versa
    private void onClickApplied() {
        if (expandableApplied.isExpanded()) {
            expandableApplied.collapse();
        } else {
            expandableApplied.expand();
        }
    }

    // If the bookmarked list is expanded, collapse and vice versa
    private void onClickBookmarked() {
        if (expandableBookmarked.isExpanded()) {
            expandableBookmarked.collapse();
        } else {
            expandableBookmarked.expand();
        }
    }

    private void collectBookmarkJobs() {
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                bookmarkList.clear();
                bookmarkListAdapter.notifyDataSetChanged();

                // For every bookmarked job, add them to the list.
                for (String jobId : user.bookmarkedJobs) {
                    addBookmarkJob(jobId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addBookmarkJob(String jobId) {
        // Find the job in the database based on the id.
        jobRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Job job = snapshot.getValue(Job.class);

                // Add the job to the list and notify the adapter that the list has changed.
                bookmarkList.add(job);
                bookmarkListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void collectAppliedJobs() {
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                applyList.clear();
                applyListAdapter.notifyDataSetChanged();

                // Loop through all jobs.
                for (DataSnapshot s : snapshot.getChildren()) {
                    Job job = s.getValue(Job.class);

                    // If the user has applied to this job, add the job to the list.
                    if (job.appliedStudents.contains(userId)) {
                        applyList.add(job);
                        applyListAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}