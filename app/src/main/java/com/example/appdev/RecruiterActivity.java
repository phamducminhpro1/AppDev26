package com.example.appdev;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdev.databinding.ActivityRecruiterBinding;

public class RecruiterActivity extends AppCompatActivity{

    private ActivityRecruiterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecruiterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // If the recruiter first logs in, the intent will contain an extra string.
        // We do this to send them to their profile on the first login, in case they want to
        // add additional information about themselves.
        if (getIntent().getStringExtra("toProfileR") != null) {
            if (getIntent().getStringExtra("toProfileR").equals("go")) {
                replaceFragment(new R_profileFragment());
                binding.rBottomNavigationView.setSelectedItemId(R.id.profile);
            }
        } else {
            replaceFragment(new S_chatFragment());
            binding.rBottomNavigationView.setSelectedItemId(R.id.chat);
        }


        binding.rBottomNavigationView.setOnItemSelectedListener(item -> {
            //Switch statement for navigation bar, replaces the fragment on click
            switch (item.getItemId()) {
                // TODO: Discuss whether recruiter and student have different chat sections.
                case R.id.chat:
                    replaceFragment(new S_chatFragment());
                    break;
                case R.id.posts:
                    replaceFragment(new R_postsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new R_profileFragment());
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
        fragmentTransaction.replace(R.id.r_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}