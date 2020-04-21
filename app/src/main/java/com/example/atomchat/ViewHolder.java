package com.example.atomchat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView message;
    TextView time;
    LinearLayout background;

    public ViewHolder(View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.message_item);
        time = itemView.findViewById(R.id.time_item);
        background = itemView.findViewById(R.id.linear_background);
    }
}
