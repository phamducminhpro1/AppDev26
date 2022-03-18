package com.example.appdev;

import android.app.DownloadManager;
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
        public TextView textTime;
        public ImageView imageMessage;

        public ViewHolder(View view) {
            super(view);

            textMessage = view.findViewById(R.id.textMessage);
            textTime = view.findViewById(R.id.textTime);
            imageMessage = view.findViewById(R.id.imageMessage);
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = mContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message msg = mChat.get(position);
        holder.textMessage.setText(msg.text);
        holder.imageMessage.setVisibility(View.INVISIBLE);

        holder.textTime.setText(msg.time);

        if (msg.imageUrl != null) {
            if (!msg.imageUrl.equals("")) {
                Glide.with(mContext).load(msg.imageUrl).into(holder.imageMessage);
                holder.imageMessage.setVisibility(View.VISIBLE);
                holder.textMessage.setVisibility(View.INVISIBLE);
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

        if (msg.fileUrl != null) {
            if (!msg.fileUrl.equals("")) {

                holder.textMessage.setText("Click to view pdf.");

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
