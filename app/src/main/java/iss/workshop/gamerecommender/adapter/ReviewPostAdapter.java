package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import iss.workshop.gamerecommender.R;

public class ReviewPostAdapter extends ArrayAdapter<Object> {

    private final Context context;
    protected List<String> titles;
    protected List<String> messages;
    protected List<String> dates;
    protected List<Boolean> reviews;
    public ReviewPostAdapter(Context context, List<String> titles, List<String> messages, List<String> dates, List<Boolean> reviews) {
        super(context, R.layout.reviewlistcell);

        this.context=context;
        this.titles=titles;
        this.messages=messages;
        this.dates=dates;
        this.reviews=reviews;


        addAll(new Object[reviews.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.reviewlistcell,parent,false);
        }

        TextView titleTextView=view.findViewById(R.id.titleTextView);
        titleTextView.setText(titles.get(pos));

        TextView dateTextView=view.findViewById(R.id.dateTextView);
        dateTextView.setText(dates.get(pos));

        TextView msgTextView=view.findViewById(R.id.messageTextView);
        msgTextView.setText(messages.get(pos));

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
}
