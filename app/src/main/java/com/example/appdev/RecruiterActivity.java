package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.appdev.databinding.ActivityRecruiterBinding;
import com.example.appdev.databinding.ActivityStudentBinding;

public class RecruiterActivity extends AppCompatActivity {

    private ActivityRecruiterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecruiterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(getIntent().getStringExtra("toProfileR") != null){
            if (getIntent().getStringExtra("toProfileR").equals("go")) {
                replaceFragment(new R_profileFragment());
            }
        }else {
            replaceFragment(new S_chatFragment());
        }


        binding.rBottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                // TODO: Discuss whether recruiter and student have different chat sections.
                case R.id.chat:
                    replaceFragment(new S_chatFragment());
                    break;
                case R.id.posts:
                    replaceFragment(new R_postsFragment());
                    break;
                // TODO: Change to new fragment --> R_profileFragment
                case R.id.profile:
                    replaceFragment(new R_profileFragment());
                    break;

            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.r_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}