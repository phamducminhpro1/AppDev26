package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

// This fragment shows recruiters a list of all their posts.
// From here they can also go to the activities to create new posts, or edit existing ones.
public class R_postsFragment extends Fragment {

    // The recycler view for the list of posted jobs.
    private RecyclerView recyclerView;
    PostListAdapter postListAdapter;
    private ArrayList<Job> jobList = new ArrayList<>();

    public R_postsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        readPosts();

        // Take the user to the activity for posting new jobs if they click on the post button.
        Button postButton = view.findViewById(R.id.buttonPost);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), JobPostActivity.class));
            }
        });

        return view;
    }

    // Reads all the posts that have been posted by this recruiter.
    private void readPosts() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jobs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list of jobs before checking for the posts.
                jobList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);

                    // If the id of the poster mathes the current user, add the job to the list.
                    if (job.posterId.equals(firebaseUser.getUid())) {
                        jobList.add(job);
                    }
                }

                // Create an adapter based on the jobList
                postListAdapter = new PostListAdapter(getContext(), jobList);
                // Set it as the new adapter for the recycler view.
                recyclerView.setAdapter(postListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // We failed to read from the database.
            }
        });
    }
}