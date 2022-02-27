package com.example.appdev;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link S_jobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class S_jobsFragment extends Fragment {

    RecyclerView recyclerView;
    String s1[], s2[];
    int images[] = {R.drawable.c_plus_plus, R.drawable.c_plus_plus, R.drawable.c_plus_plus,R.drawable.kotlin};
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
        s1 = getResources().getStringArray(R.array.programming_languages);
        s2 = getResources().getStringArray(R.array.description);
        if (getArguments() != null) {
            mParam1 =  getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

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
        MyAdapter myAdapter = new MyAdapter(getContext(), s1, s2, images);
        recyclerView.setAdapter(myAdapter);
        return view;
    }
}