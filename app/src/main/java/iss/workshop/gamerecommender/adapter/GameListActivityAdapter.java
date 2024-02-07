package iss.workshop.gamerecommender.adapter;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import iss.workshop.gamerecommender.R;

public class GameListActivityAdapter extends ArrayAdapter<Object> {
    private final Context context;
    protected List<String> urls;
    protected List<String> titles;
    public GameListActivityAdapter(Context context, List<String> urls, List<String> titles) {
        super(context, R.layout.gamelistcell);

        this.context=context;
        this.urls=urls;
        this.titles=titles;

        addAll(new Object[titles.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view==null){
            LayoutInflater inflater=
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.gamelistcell,parent,false);
        }

        TextView textView=view.findViewById(R.id.textview);
        textView.setText(titles.get(pos));

        ImageView imageView=view.findViewById(R.id.imageview);
        String url = urls.get(pos);
        ImageLoader.loadImage(context, url, imageView);

        return view;
    }
}