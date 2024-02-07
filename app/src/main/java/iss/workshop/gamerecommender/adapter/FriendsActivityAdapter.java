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

public class FriendsActivityAdapter extends ArrayAdapter<Object>  {
    private final Context context;
    protected List<String> urls;
    protected List<String> names;
    public FriendsActivityAdapter(Context context, List<String> urls, List<String> names) {
        super(context, R.layout.friendlistcell);

        this.context=context;
        this.urls=urls;
        this.names=names;

        addAll(new Object[names.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.friendlistcell,parent,false);
        }


        TextView textView=view.findViewById(R.id.textview);
        textView.setText(names.get(pos));

        ImageView imageView=view.findViewById(R.id.imageview);
        String url = urls.get(pos);
        ImageLoader.loadImage(context, url, imageView);

        return view;
    }
}
