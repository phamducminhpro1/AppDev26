package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Job> jobList;
    private ArrayList<Job> jobListFull;
    private Context context;
    public JobListAdapter(Context context, ArrayList<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
        jobListFull = new ArrayList<>(jobList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_job_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {
        Job currentItem = jobList.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.description.setText(currentItem.getDescription());
        if(currentItem.imageUrl != null) {
            if (!currentItem.imageUrl.isEmpty()) {
                Glide.with(context).load(currentItem.imageUrl).into(holder.mImageView);
            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.bookmarkedJobs.contains(currentItem.id)) {
                    holder.bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                } else {
                    holder.bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);
                }

                reference.child(userId).setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        int finalPosition = position;
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, JobDescriptionActivity.class);
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

    public Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Job> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(jobListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                for (Job item : jobListFull) {
                    if (item.getTitle().toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            jobList.clear();
            jobList.addAll((ArrayList<Job>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView title;
        public TextView description;
        ConstraintLayout mainLayout;
        Button bookmarkButton;

    public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.myTextView1);
            description = itemView.findViewById(R.id.myTextView2);
            mImageView = itemView.findViewById(R.id.myImageView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Job jobItem = jobListFull.get(position);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user.bookmarkedJobs.contains(jobItem.id)) {
                                user.bookmarkedJobs.remove(jobItem.id);
                                bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);
                            } else {
                                user.bookmarkedJobs.add(jobItem.id);
                                bookmarkButton.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
                            }

                            reference.child(userId).setValue(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
    }
}
