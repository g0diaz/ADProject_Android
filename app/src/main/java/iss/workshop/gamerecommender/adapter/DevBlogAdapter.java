package iss.workshop.gamerecommender.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import iss.workshop.gamerecommender.R;

public class DevBlogAdapter extends ArrayAdapter<Object> {

    private final Context context;
    protected List<String> titles;
    protected List<String> messages;
    protected List<String> dates;
    public DevBlogAdapter(Context context, List<String> titles, List<String> messages, List<String> dates) {
        super(context, R.layout.bloglistcell);

        this.context=context;
        this.titles=titles;
        this.messages=messages;
        this.dates=dates;


        addAll(new Object[titles.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.bloglistcell,parent,false);
        }

        TextView titleTextView=view.findViewById(R.id.titleTextView);
        titleTextView.setText(titles.get(pos));

        TextView dateTextView=view.findViewById(R.id.dateTextView);
        dateTextView.setText(dates.get(pos));

        TextView msgTextView=view.findViewById(R.id.messageTextView);
        msgTextView.setText(messages.get(pos));

        return view;
    }
}
