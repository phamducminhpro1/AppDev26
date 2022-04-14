package com.example.appdev;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    // Constants to indicate whether a message should be shown on the left or right
    // This depends on whether the message was sent or received by the user.
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mChat;

    FirebaseUser fUser;

    public MessageAdapter(Context context, List<Message> chat) {
        mContext = context;
        mChat = chat;
    }

    // Describes all the UI elements of a message bubble.
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textMessage;
        public TextView textTime;
        public ImageView imageMessage;

        public ViewHolder(View view) {
            super(view);

            // Gather all elements from the view.
            textMessage = view.findViewById(R.id.textMessage);
            textTime = view.findViewById(R.id.textTime);
            imageMessage = view.findViewById(R.id.imageMessage);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        // Check whether the message should be on the left or the right.
        if (viewType == MSG_TYPE_LEFT) {
            // Use the left layout
            view = LayoutInflater.from(mContext).inflate(R.layout.msg_left_item, parent, false);
        } else {
            // Use the right layout
            view = LayoutInflater.from(mContext).inflate(R.layout.msg_right_item, parent, false);
        }

        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message msg = mChat.get(position);

        // Set the message and time.
        holder.textMessage.setText(msg.text);
        holder.textTime.setText(msg.time);

        // Set the image placeholder to invisible by default.
        holder.imageMessage.setVisibility(View.GONE);


        // Check if there is an image in the message.
        if (msg.imageUrl != null) {
            if (!msg.imageUrl.equals("")) {
                // Load the image.
                Glide.with(mContext).load(msg.imageUrl).into(holder.imageMessage);

                // Make the image visible
                holder.imageMessage.setVisibility(View.VISIBLE);
                holder.textMessage.setVisibility(View.GONE);

                // Upon clicking the image, the user should be taken to a full screen view of the image.
                holder.imageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, FullscreenImageActivity.class);
                        intent.putExtra("imageUrl", msg.imageUrl);
                        mContext.startActivity(intent);
                    }
                });
            }
        }

        // Check whether there is a file attached to the message.
        if (msg.fileUrl != null) {
            if (!msg.fileUrl.equals("")) {

                holder.textMessage.setText("Click to view pdf.");

                // Upon clicking the message, the file should be opened in a pdf viewer.
                holder.textMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(msg.fileUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Intent newIntent = Intent.createChooser(intent, "Open File");
                        mContext.startActivity(newIntent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    // Returns whether the image should be on the left or right.
    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        // If the user is the sender, put the message on the right, else it should be on the left.
        if (mChat.get(position).sender.equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
