package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.appdev.databinding.ActivityStudentBinding;

public class StudentActivity extends AppCompatActivity {

    private ActivityStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(getIntent().getStringExtra("toProfileS") != null) {
            if (getIntent().getStringExtra("toProfileS").equals("go")) {
                replaceFragment(new S_profileFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.profile);
            }
        } else{
            replaceFragment(new S_chatFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.chat);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            //Switch statement for navigation bar, replaces the fragment on click
            switch (item.getItemId()){

                case R.id.chat:
                    replaceFragment(new S_chatFragment());
                    break;
                case R.id.bookmark:
                    replaceFragment(new S_bookmarkFragment());
                    break;
                case R.id.jobs:
                    replaceFragment(new S_jobsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new S_profileFragment());
                    break;

            }
            return true;
        });
    }

    //Method to replace current fragment to input fragment
    //Input is a fragment
    //Output none
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}