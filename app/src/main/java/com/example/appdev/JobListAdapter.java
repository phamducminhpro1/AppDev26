package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

// This adapter is used to show jobs in a list to students.
// The adapter is used on the bookmark page and the jobs page.
public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.MyViewHolder> implements Filterable {
    // The filtered list of jobs.
    private ArrayList<Job> jobList;
    // The full list with all jobs.
    private ArrayList<Job> jobListFull;

    private Context context;
    private int buttonState = 0;
    // 0 is buttonState for title
    // 1 is buttonState for description
    // 2 is buttonState for companyName

    public JobListAdapter(Context context, ArrayList<Job> jobList) {
        this.context = context;
        this.jobListFull = new ArrayList<>(jobList);
        this.jobList = jobList;
    }

    public void setButtonState(int num) {
        this.buttonState = num;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_job_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {
        // Get a specific job from the list.
        Job currentItem = jobList.get(position);

        // Load the job information into the UI elements.
        holder.title.setText(currentItem.title);
        holder.company.setText(currentItem.company);

        // If the job has a date, show that date.
        if (currentItem.date != null) {
            holder.date.setText(currentItem.date);
        } else {
            holder.date.setText("");
        }

        // If the job listing has an image load it instead of the placeholder image.
        if(currentItem.imageUrl != null) {
            if (!currentItem.imageUrl.isEmpty()) {
                Glide.with(context).load(currentItem.imageUrl).into(holder.mImageView);
            }
        }

        // Database reference to all users.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        // Get the user id.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Use the user id to find the current user in the database reference.
        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                // Change the color of the bookmark button depending
                // on whether the user has the job bookmarked or not.
                if (user.bookmarkedJobs.contains(currentItem.id)) {
                    holder.bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                } else {
                    holder.bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // When clicking on a job, take the user to a detailed description of the job.
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Put the job id in the intent so the next activity knows for which job we want to see.
                Intent intent = new Intent(context, JobDescriptionActivity.class);
                intent.putExtra("jobId", currentItem.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    // Filter for filtering the job based on the title, company name or description.
    public Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Job> filteredList = new ArrayList<>();

            // If there is no input, show all jobs.
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(jobListFull);
            } else {
                // Loop through all jobs.
                for (Job item : jobListFull) {
                    if (buttonState == 0) {
                        // Filter on title
                        if (item.title.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            filteredList.add(item);
                        }
                    } else if (buttonState == 1) {
                        // Filter on description
                        if (item.description.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            filteredList.add(item);
                        }
                    } else if (buttonState == 2) {
                        // Filter on company name
                        if (item.company.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            filteredList.add(item);
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // Update the list with the new results after filtering.
            jobList.clear();
            jobList.addAll((ArrayList<Job>) filterResults.values);

            // Notify the adapter and recyclerView that the list has changed.
            notifyDataSetChanged();
        }
    };

    // Describes all UI elements of a single item in the list.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView title;
        public TextView company;
        public TextView date;
        ConstraintLayout mainLayout;
        Button bookmarkButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Gather all the UI elements from the itemView.
            title = itemView.findViewById(R.id.myTextView1);
            company = itemView.findViewById(R.id.myTextView2);
            date = itemView.findViewById(R.id.myTextView3);
            mImageView = itemView.findViewById(R.id.myImageView);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            // Set the behavior for clicking on the bookmark button.
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the job for which the user clicked the bookmark button.
                    int position = getAdapterPosition();
                    Job jobItem = jobList.get(position);

                    // Find the user in the database.
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);

                            // Add or remove the job to the bookmarked jobs.
                            if (user.bookmarkedJobs.contains(jobItem.id)) {
                                user.bookmarkedJobs.remove(jobItem.id);
                            } else {
                                user.bookmarkedJobs.add(jobItem.id);
                            }

                            // Update the value of the user in the database with the new bookmarked jobs.
                            reference.child(userId).setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to use the database.
                        }
                    });
                }
            });
        }
    }
}
