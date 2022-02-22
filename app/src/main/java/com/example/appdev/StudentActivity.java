package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.appdev.databinding.ActivityStudentBinding;

public class StudentActivity extends AppCompatActivity {

    ActivityStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new S_homeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.home:
                    replaceFragment(new S_homeFragment());
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