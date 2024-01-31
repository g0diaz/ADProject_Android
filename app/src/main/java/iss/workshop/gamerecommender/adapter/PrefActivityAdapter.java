package iss.workshop.gamerecommender.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import iss.workshop.gamerecommender.R;

public class PrefActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;
    protected String[] genres;
    public PrefActivityAdapter(Context context, String[] genres) {
        super(context, R.layout.prefcell);

        this.context=context;
        this.genres=genres;

        addAll(new Object[genres.length]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.prefcell,parent,false);
        }

        CheckBox checkBox=view.findViewById(R.id.checkbox);
        checkBox.setText(genres[pos]);

        return view;
    }
}