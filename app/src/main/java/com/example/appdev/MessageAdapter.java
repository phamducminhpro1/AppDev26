package com.example.appdev;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mChat;

    FirebaseUser fUser;

    public MessageAdapter(Context context, List<Message> chat) {
        mContext = context;
        mChat = chat;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textMessage;

        public ViewHolder(View view) {
            super(view);

            textMessage = view.findViewById(R.id.textMessage);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.msg_left_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.msg_right_item, parent, false);
        }

        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message msg = mChat.get(position);
        holder.textMessage.setText(msg.text);
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).sender.equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
