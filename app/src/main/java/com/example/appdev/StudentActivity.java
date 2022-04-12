package com.example.appdev;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdev.databinding.ActivityStudentBinding;

/*
 General activity for the student, within this activity the different fragments will be loaded.
 */
public class StudentActivity extends AppCompatActivity {

    private ActivityStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // If the student first logs in, the intent will contain an extra string.
        // We do this to send them to their profile on the first login, in case they want to
        // add additional information about themselves.
        if(getIntent().getStringExtra("toProfileS") != null) {
            if (getIntent().getStringExtra("toProfileS").equals("go")) {
                replaceFragment(new S_profileFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.profile);
            }
        } else {
            // If it's not the first time logging in, load the chat page.
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