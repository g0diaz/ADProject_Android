package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import iss.workshop.gamerecommender.R;

public class FeedActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;

    protected String[] feeds;

    public FeedActivityAdapter(Context context, String[] feeds) {
        super(context, R.layout.row);
        this.context = context;
        this.feeds = feeds;

        addAll(new Object[feeds.length]);
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.row, parent, false);
        }

        // set the text for TextView
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(feeds[pos]);

        return view;
    }
}
