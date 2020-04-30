package com.example.atomchat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView message;
    TextView time;
    LinearLayout background;
    //RelativeLayout relative_seen;
    ImageView eye_seen;
    ImageView eye_not_seen;

    public ViewHolder(View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.message_item);
        time = itemView.findViewById(R.id.time_item);
        background = itemView.findViewById(R.id.linear_background);
        eye_seen = itemView.findViewById(R.id.eye_seen);
        eye_not_seen = itemView.findViewById(R.id.eye_not_seen);
        //relative_seen = itemView.findViewById(R.id.relative_for_seen);
    }
}
