package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;

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

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link S_jobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class S_jobsFragment extends Fragment {
    private RecyclerView recyclerView;
    JobListAdapter jobListAdapter;
    private ArrayList<Job> jobList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public S_jobsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment S_jobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static S_jobsFragment newInstance(String param1, String param2) {
        S_jobsFragment fragment = new S_jobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.jobList.add(new Job(R.drawable.c_plus_plus, "C++", "This is random description"));
        this.jobList.add(new Job(R.drawable.java, "Java", "This is random description"));
        this.jobList.add(new Job(R.drawable.kotlin, "Kotlin", "This is random description"));
        this.jobList.add(new Job(R.drawable.tue_jobs, "Tue", "This is random description"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_jobs, container, false);
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_s_jobs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        jobListAdapter = new JobListAdapter(getContext(), jobList);
        recyclerView.setAdapter(jobListAdapter);
        setHasOptionsMenu(true);
        Button mapsButton = view.findViewById(R.id.mapsButton);
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.s_jobs_toolbar);
        ((StudentActivity) getActivity()).setSupportActionBar(myToolbar);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
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