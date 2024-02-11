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

public class FriendProfileDevelopersAdapter extends ArrayAdapter<Object>  {
    private final Context context;
    protected List<String> names;
    public FriendProfileDevelopersAdapter(Context context, List<String> names) {
        super(context, R.layout.developerlistcell);

        this.context=context;
        this.names=names;

        addAll(new Object[names.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.developerlistcell,parent,false);
        }


        TextView textView=view.findViewById(R.id.textview);
        textView.setText(names.get(pos));

        return view;
    }
}