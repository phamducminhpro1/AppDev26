package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

// Adapter which will show the recruiter the jobs they have posted.
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.MyViewHolder> {
    // List of posted jobs.
    private ArrayList<Job> postList;
    private Context context;

    public PostListAdapter(Context context, ArrayList<Job> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {
        // Get a specific job from the list.
        Job job = postList.get(position);

        // Load the job information into the UI elements.
        holder.title.setText(job.title);
        holder.company.setText(job.company);

        // If the job listing has an image load it instead of the placeholder image.
        if (job.imageUrl != null) {
            if (!job.imageUrl.isEmpty()) {
                Glide.with(context).load(job.imageUrl).into(holder.image);
            }
        }

        // When clicking on the job, take the recruiter to an activity with a list of applicants.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Put the job id in the intent so the next activity knows for which job it should show applicants.
                Intent intent = new Intent(context, ApplicantListActivity.class);
                intent.putExtra("jobId", job.id);
                context.startActivity(intent);
            }
        });

        // When clicking on the edit button take the recruiter to the edit activity.
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Put the job id in the intent so the next activity knows which job we want to edit.
                Intent intent = new Intent(context, EditPostActivity.class);
                intent.putExtra("jobId", job.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Describes all the UI elements of a single item in the list of jobs.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView company;
        public Button edit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Gather the UI elements from the itemView.
            title = itemView.findViewById(R.id.postTitle);
            company = itemView.findViewById(R.id.postCompany);
            image = itemView.findViewById(R.id.postImage);
            edit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}
