package com.example.appdev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.MyViewHolder> {
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
        Job job = postList.get(position);
        holder.title.setText(job.title);
        holder.company.setText(job.company);
        if (job.imageUrl != null) {
            if (!job.imageUrl.isEmpty()) {
                Glide.with(context).load(job.imageUrl).into(holder.image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView company;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            company = itemView.findViewById(R.id.postCompany);
            image = itemView.findViewById(R.id.postImage);
        }
    }
}
