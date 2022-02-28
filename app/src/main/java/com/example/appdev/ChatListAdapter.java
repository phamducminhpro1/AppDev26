package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    public ChatListAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;

        public ViewHolder(View view) {
            super(view);

            username = view.findViewById(R.id.textUsername);
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
        String displayName = user.firstName + " " + user.lastName;
        holder.username.setText(displayName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
