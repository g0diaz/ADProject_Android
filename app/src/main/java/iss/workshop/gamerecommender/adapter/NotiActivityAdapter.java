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

public class NotiActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;
    protected String[] notifications;

    public NotiActivityAdapter(Context context, String[] notifications) {
        super(context, R.layout.feedcell);
        this.context = context;
        this.notifications = notifications;

        addAll(new Object[notifications.length]);
    }

    @NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.noticell, parent, false);
        }

        TextView textView = view.findViewById(R.id.noti);
        textView.setText(notifications[pos]);

        return view;
    }
}
