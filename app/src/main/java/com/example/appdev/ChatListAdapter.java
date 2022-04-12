package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/*
This adapter will be used everywhere where,
we want to show a list of people whom you can start a chat with.
It will show their profile picture, full name and on clicking the user will go the chat with them.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> implements Filterable {
    private Context mContext;

    // The full list of all users in case there is no filtering.
    private List<User> mUsersFull;

    // The filtered list of users depending on the search bar.
    private List<User> mUsers;

    public ChatListAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
        mUsersFull = new ArrayList<>(users);
    }

    // The Viewholder describes an item of this adapter.
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public CircleImageView imageProfile;

        public ViewHolder(View view) {
            super(view);

            username = view.findViewById(R.id.textUsername);
            imageProfile = view.findViewById(R.id.imageProfile);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);

        // Combine their first and last name and show it.
        String displayName = user.firstName + " " + user.lastName;
        holder.username.setText(displayName);

        // If they don't have a profile picture use the default image.
        holder.imageProfile.setImageResource(R.drawable.ic_baseline_person_24);

        // If there is a profile picture, we load this into the correct place.
        if (user.imageUrl != null) {
            if (!user.imageUrl.equals("")) {
                Glide.with(mContext).load(user.imageUrl).into(holder.imageProfile);
            }
        }

        // On clicking the item, we switch to the messaging activity.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent includes the userid of the person we want to chat with.
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return chatFilter;
    }

    public Filter chatFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filteredList = new ArrayList<>();

            // If we are not searching for anything, show all users.
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(mUsersFull);
            } else {
                // Loop through all users.
                for (User item : mUsersFull) {
                    // If their name matches what was typed in the filter we add them to the list.
                    String fullName = item.firstName + " " + item.lastName;
                    if (fullName.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
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
            // Clear the list of filtered users.
            mUsers.clear();
            // Add all the new users and let the view know the list has changed.
            mUsers.addAll((ArrayList<User>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
