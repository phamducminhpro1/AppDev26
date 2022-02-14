package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private String databaseUrl = "https://todolist-d7e89-default-rtdb.europe-west1.firebasedatabase.app/";

    private ArrayList<Task> taskList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rvTasks);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance(databaseUrl).getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    taskList = new ArrayList<>(userProfile.tasks);
                    TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter(taskList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                taskList = new ArrayList<>();
                TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter(taskList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public void addTask(View view) {
        EditText textViewNewTask = (EditText)findViewById(R.id.editTextNewTask);
        String taskTitle = textViewNewTask.getText().toString();
        if (taskTitle.equals("")) {
            return;
        }

        taskList.add(new Task(taskTitle));
        reference.child(userID).child("tasks").setValue(taskList);
        recyclerView.getAdapter().notifyItemInserted(taskList.size() - 1);
    }

    public void removeTask(View view) {
        int index = recyclerView.getChildAdapterPosition((View)view.getParent());
        taskList.remove(index);
        reference.child(userID).child("tasks").setValue(taskList);
        recyclerView.getAdapter().notifyItemRemoved(index);
    }
}