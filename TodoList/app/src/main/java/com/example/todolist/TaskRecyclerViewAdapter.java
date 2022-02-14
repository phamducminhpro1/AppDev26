package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;

    public TaskRecyclerViewAdapter(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView taskText;
        private int index;

        public TaskViewHolder(final View view) {
            super(view);
            taskText = view.findViewById(R.id.tvTaskName);
        }
    }

    @NonNull
    @Override
    public TaskRecyclerViewAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerViewAdapter.TaskViewHolder holder, int position) {
        String title = taskList.get(position).getTitle();
        holder.taskText.setText(title);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
