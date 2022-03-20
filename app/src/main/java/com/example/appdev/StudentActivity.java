package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}