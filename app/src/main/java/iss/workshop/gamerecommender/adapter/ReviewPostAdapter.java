package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import iss.workshop.gamerecommender.R;

public class ReviewPostAdapter extends ArrayAdapter<Object> {
    private final Context context;
    protected List<String> messages;
    protected List<String> dates;
    protected List<Boolean> reviews;
    protected List<String> usernames;
    protected List<Integer> userIds;
    private OnItemClickListener listener;
    private int myUserId;
    public ReviewPostAdapter(Context context, List<String> messages, List<String> dates, List<Boolean> reviews, List<String> usernames, List<Integer> userIds) {
        super(context, R.layout.reviewlistcell);

        this.context=context;
        this.messages=messages;
        this.dates=dates;
        this.reviews=reviews;
        this.usernames=usernames;
        this.userIds=userIds;

        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        myUserId = sharedPreferences.getInt("userId", -1);
        addAll(new Object[reviews.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.reviewlistcell,parent,false);
        }

        Button deleteButton = view.findViewById(R.id.deleteBtn);
        if (userIds.get(pos) == myUserId) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(pos);
                }
            }
        });

        TextView dateTextView=view.findViewById(R.id.dateTextView);
        dateTextView.setText(dates.get(pos));

        TextView msgTextView=view.findViewById(R.id.messageTextView);
        msgTextView.setText(messages.get(pos));

        TextView userTextView=view.findViewById(R.id.reviewUserTextView);
        userTextView.setText(usernames.get(pos));
        userTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(pos);
                }
            }
        });

        TextView reviewTextView=view.findViewById(R.id.reviewTextView);

        if (reviews.get(pos)) {
            reviewTextView.setText("Recommended");
            reviewTextView.setTextColor(Color.rgb(0, 255, 0));
        } else {
            reviewTextView.setText("Not Recommended");
            reviewTextView.setTextColor(Color.rgb(255, 0, 0));
        }

        return view;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
