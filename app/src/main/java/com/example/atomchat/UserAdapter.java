package com.example.atomchat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private FirebaseUser user;

    ArrayList<User> users;
    LayoutInflater inflater;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        //holder.username.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(user.getProfile_color()), Color.parseColor(user.getProfile_color())));
        holder.profile_image.setColorFilter(Color.parseColor(user.getProfile_color()));
        //holder.profile_image.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor(user.getProfile_color()), Color.parseColor(user.getProfile_color())));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}

