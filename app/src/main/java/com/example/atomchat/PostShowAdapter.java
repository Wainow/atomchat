package com.example.atomchat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PostShowAdapter extends RecyclerView.Adapter<PostShowAdapter.ViewHolder> {
    private FirebaseUser user;

    private static final int MSG_TYPE_POST = 0;
    private static final int MSG_TYPE_ANSWER = 1;

    ArrayList<ChatPost> messages;
    LayoutInflater inflater;

    public PostShowAdapter(Context context, ArrayList<ChatPost> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_POST){
            View view = inflater.inflate(R.layout.item_post_show, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_post, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatPost chat = messages.get(position);
        holder.text.setText(chat.getText());
        holder.data.setText(chat.getData());
        holder.author.setText(chat.getAuthor());
        if(getItemViewType(position) == MSG_TYPE_ANSWER){
            holder.linearLayout.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(userColor(chat.getAuthor())),Color.parseColor(userColor(chat.getAuthor()))));
        }
        if(chat.getImageURL() != null){
            Glide.with(inflater.getContext()).load(chat.getImageURL()).into(holder.image);
            holder.image.setClipToOutline(true);
        }
        if(position == 0){
            //holder.text_answers.setTextColor(Color.parseColor(userColor(chat.getAuthor())));
            holder.author.setTextColor(Color.parseColor(userColor(chat.getAuthor())));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(position==0){
            return MSG_TYPE_POST;
        } else {
            return  MSG_TYPE_ANSWER;
        }
    }

    public String userColor(String id) {

        String color = "";
        String norm = "1234567890ABCDEFabcdef";
        int n = 0;
        for (int i = 0; i < id.length() && n < 6; i++) {
            for (int j = 0; j < 22; j++) {
                if (id.charAt(i) == norm.charAt(j)) {
                    if (j < 16) color = color + norm.charAt(j);
                    else color = color + norm.charAt(j - 6);
                    n++;
                }
            }
        }
        while (n++ < 6) color = color + '0';
        color = "#" + color;
        return color;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView author;
        public TextView data;
        public TextView text;
        public TextView text_answers;
        public ImageView image;
        public LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.username_post_show);
            data = itemView.findViewById(R.id.data_post_show);
            text = itemView.findViewById(R.id.text_post_show);
            image = itemView.findViewById(R.id.post_image_show);
            linearLayout = itemView.findViewById(R.id.linear_background);
            text_answers = itemView.findViewById(R.id.text_post_answers);
        }
    }
}
