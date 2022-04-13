package com.example.appdev;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class S_jobsFragment extends Fragment {
    // RecylerView variable
    private RecyclerView recyclerView;
    // JobListAdapter variable
    JobListAdapter jobListAdapter;
    // Array list of jobs
    private ArrayList<Job> jobList = new ArrayList<>();
    // variable for the database reference
    DatabaseReference reference;

    public S_jobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        reference = FirebaseDatabase.getInstance().getReference("Jobs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear job List
                jobList.clear();
                // Add the jobs from the database
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Job job = dataSnapshot.getValue(Job.class);
                    jobList.add(job);
                }
                //Put the job list into the job list adapter
                jobListAdapter = new JobListAdapter(getContext(), jobList);
                recyclerView.setAdapter(jobListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // Function to chang the color or the button
    // Parameter we have the buttonClicked (This is the button the the users press) and the array of
    // of other buttons
    public void buttonColorChange(Button buttonClicked, Button[] otherButton) {
        // Set button style to solid
        buttonClicked.setBackgroundResource(R.drawable.round_button_solid);
        // Set the text color to white
        buttonClicked.setTextColor(Color.WHITE);
        // This for loop will chang the color of other buttons
        for (int i = 0; i < otherButton.length; i++) {
            otherButton[i].setBackgroundResource(R.drawable.round_button_hollow);
            // Set the text color of other button
            otherButton[i].setTextColor(0xff0E67B4);
        }
    }

    //onCreateView function takes inflater and container, savedInstanceState as the parameter
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_s_jobs, container, false);
        // Find the recycler view
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // Find the maps buttons
        Button mapsButton = view.findViewById(R.id.mapsButton);
        // Find the toobar
        Toolbar myToolbar = view.findViewById(R.id.s_jobs_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        //Set listener for buttons
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
        // FInd title button
        Button titleButton = view.findViewById(R.id.titleButton);
        // FInd description button
        Button descriptionButton = view.findViewById(R.id.descriptionButton);
        // FInd company name button
        Button companyNameButton = view.findViewById(R.id.companyButton);
        // Set listener for title button
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set button state
                jobListAdapter.setButtonState(0);
                Button[] otherButton = {descriptionButton, companyNameButton};
                buttonColorChange(titleButton, otherButton);            ;
            }
        });
        // Set listener for description button
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set button state
                jobListAdapter.setButtonState(1);
                Button[] otherButton = {titleButton, companyNameButton};
                buttonColorChange(descriptionButton, otherButton);
            }
        });
        //Set listener for company name button
        companyNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set button state
                jobListAdapter.setButtonState(2);
                Button[] otherButton = {titleButton, descriptionButton};
                buttonColorChange(companyNameButton, otherButton);
                buttonColorChange(companyNameButton, otherButton);
            }
        });
        return view;
    }

    //Function to for the search view
    // Function take Menu and inflatter as the parameter
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear the meny
        menu.clear();
        // Inflate the search bar inot the nav abr
        inflater.inflate(R.menu.search_menu, menu);
        // Variable of menuItem for the action search bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Get the action view
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Set the listener to the search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // WHen the user type, we filter the job list
            @Override
            public boolean onQueryTextChange(String newText) {
                jobListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }



}