package com.example.appdev;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

public class S_jobsFragment extends Fragment {
    private RecyclerView recyclerView;
    JobListAdapter jobListAdapter;
    private ArrayList<Job> jobList = new ArrayList<>();

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

                jobList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Job job = dataSnapshot.getValue(Job.class);
                    jobList.add(job);
                }

                jobListAdapter = new JobListAdapter(getContext(), jobList);
                recyclerView.setAdapter(jobListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void buttonColorChange(Button buttonClicked, Button[] otherButton) {
        buttonClicked.setBackgroundResource(R.drawable.round_button_solid);
        buttonClicked.setTextColor(Color.WHITE);
        for (int i = 0; i < otherButton.length; i++) {
            otherButton[i].setBackgroundResource(R.drawable.round_button_hollow);
            otherButton[i].setTextColor(0xff0E67B4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_jobs, container, false);
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_s_jobs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        setHasOptionsMenu(true);
        Button mapsButton = view.findViewById(R.id.mapsButton);

        // This is a circular dependency, StudentActivity uses s_jobsFragment
        // and because of this line, s_jobsFragment uses StudentActivity.
        // I don't think we need this line, so for now I just commented it (Kay)
        // Toolbar myToolbar = view.findViewById(R.id.s_jobs_toolbar);
        // ((StudentActivity)getActivity()).setSupportActionBar(myToolbar);

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
        Button titleButton = view.findViewById(R.id.titleButton);
        Button descriptionButton = view.findViewById(R.id.descriptionButton);
        Button companyNameButton = view.findViewById(R.id.companyButton);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobListAdapter.setButtonState(0);
                Button[] otherButton = {descriptionButton, companyNameButton};
                buttonColorChange(titleButton, otherButton);            ;
            }
        });
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobListAdapter.setButtonState(1);
                Button[] otherButton = {titleButton, companyNameButton};
                buttonColorChange(descriptionButton, otherButton);
            }
        });
        companyNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobListAdapter.setButtonState(2);
                Button[] otherButton = {titleButton, descriptionButton};
                buttonColorChange(companyNameButton, otherButton);
                buttonColorChange(companyNameButton, otherButton);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                jobListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }



}